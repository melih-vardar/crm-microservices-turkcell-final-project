package com.turkcell.customer_support_service.service.impl;

import com.turkcell.customer_support_service.entity.CustomerSupport;
import com.turkcell.customer_support_service.repository.CustomerSupportRepository;
import com.turkcell.customer_support_service.rules.CustomerSupportBusinessRules;
import com.turkcell.customer_support_service.service.CustomerSupportService;
import io.github.bothuany.dtos.support.TicketCreateDTO;
import io.github.bothuany.dtos.support.TicketResponseDTO;
import io.github.bothuany.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService {

    private final CustomerSupportRepository customerSupportRepository;
    private final CustomerSupportBusinessRules customerSupportBusinessRules;

    @Override
    public TicketResponseDTO createTicket(TicketCreateDTO ticket) {
        customerSupportBusinessRules.checkIfCustomerExists(ticket.getCustomerId());

        CustomerSupport customerSupport = new CustomerSupport();
        updateTicketFromRequest(customerSupport, ticket);
        CustomerSupport SavedCustomerSupport = customerSupportRepository.save(customerSupport);
        return convertToResponseDTO(SavedCustomerSupport);
    }

    @Override
    public TicketResponseDTO updateTicket(UUID id, TicketCreateDTO ticket) {
        customerSupportBusinessRules.checkIfTicketExists(id);
        customerSupportBusinessRules.checkIfCustomerExists(ticket.getCustomerId());

        CustomerSupport customerSupport = customerSupportRepository.findById(id).get();
        updateTicketFromRequest(customerSupport, ticket);
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
        return customerSupportRepository.findAll().stream().map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTicket(UUID id) {
        customerSupportBusinessRules.checkIfTicketExists(id);
        CustomerSupport customerSupport = customerSupportRepository.findById(id).get();
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
}
