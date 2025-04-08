package com.turkcell.customer_support_service.service.impl;

import com.turkcell.customer_support_service.client.CustomerClient;
import com.turkcell.customer_support_service.entity.CustomerSupport;
import com.turkcell.customer_support_service.repository.CustomerSupportRepository;
import com.turkcell.customer_support_service.rules.CustomerSupportBusinessRules;
import com.turkcell.customer_support_service.service.CustomerSupportService;
import io.github.bothuany.dtos.support.TicketCreateDTO;
import io.github.bothuany.dtos.support.TicketResponseDTO;
import io.github.bothuany.enums.TicketStatus;
import io.github.bothuany.event.analytics.TicketAnalyticsEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService {

    private final CustomerSupportRepository customerSupportRepository;
    private final CustomerSupportBusinessRules customerSupportBusinessRules;
    private final StreamBridge streamBridge;
    private static final Logger logger = LoggerFactory.getLogger(CustomerSupportServiceImpl.class);

    @Override
    public TicketResponseDTO createTicket(TicketCreateDTO ticket) {
        customerSupportBusinessRules.checkIfCustomerExists(ticket.getCustomerId());

        CustomerSupport customerSupport = new CustomerSupport();
        updateTicketFromRequest(customerSupport, ticket);
        sendTicketForAnalytics(customerSupport,"TICKET_CREATED");
        return convertToResponseDTO(customerSupportRepository.save(customerSupport));
    }

    @Override
    public TicketResponseDTO updateTicket(UUID id, TicketCreateDTO ticket) {
        customerSupportBusinessRules.checkIfTicketExists(id);
        customerSupportBusinessRules.checkIfCustomerExists(ticket.getCustomerId());

        CustomerSupport customerSupport = customerSupportRepository.findById(id).get();
        updateTicketFromRequest(customerSupport, ticket);
        sendTicketForAnalytics(customerSupport,"TICKET_UPDATED");
        return convertToResponseDTO(customerSupportRepository.save(customerSupport));
    }

    @Override
    public List<TicketResponseDTO> getTicketsByCustomerId(UUID customerId) {
        customerSupportBusinessRules.checkIfCustomerExists(customerId);

        return customerSupportRepository.getAllByCustomerId(customerId);
    }

    @Override
    public TicketResponseDTO getTicketById(UUID id) {
        customerSupportBusinessRules.checkIfTicketExists(id);

        return convertToResponseDTO(customerSupportRepository.findById(id).get());
    }

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        return customerSupportRepository.findAll().stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteTicket(UUID id) {
        customerSupportBusinessRules.checkIfTicketExists(id);
        CustomerSupport customerSupport = customerSupportRepository.findById(id).get();
        sendTicketForAnalytics(customerSupport,"TICKET_CLOSED");
        customerSupportRepository.deleteById(id);
    }

    private void updateTicketFromRequest(CustomerSupport customerSupport, TicketCreateDTO ticketCreateDTO) {
        customerSupport.setStatus(TicketStatus.OPEN);
        customerSupport.setDescription(ticketCreateDTO.getDescription());
        customerSupport.setCustomerId(ticketCreateDTO.getCustomerId());
        customerSupport.setIssueType(ticketCreateDTO.getIssueType());
    }

    private TicketResponseDTO convertToResponseDTO(CustomerSupport customerSupport) {
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setCustomerId(customerSupport.getCustomerId());
        ticketResponseDTO.setId(customerSupport.getId());
        ticketResponseDTO.setStatus(customerSupport.getStatus());
        ticketResponseDTO.setIssueType(customerSupport.getIssueType());

        return ticketResponseDTO;
    }

    private void sendTicketForAnalytics(CustomerSupport customerSupport,String eventName){
        TicketAnalyticsEvent ticketAnalyticsEvent = new TicketAnalyticsEvent();
        ticketAnalyticsEvent.setTicketId(customerSupport.getId());
        ticketAnalyticsEvent.setCustomerId(customerSupport.getCustomerId());
        ticketAnalyticsEvent.setEventType(eventName);
        ticketAnalyticsEvent.setEventTime(LocalDateTime.now());

        logger.info("sending ticket for customer support {}",customerSupport.getCustomerId());
        streamBridge.send("TicketAnalytics-out-0",ticketAnalyticsEvent);


    }
}
