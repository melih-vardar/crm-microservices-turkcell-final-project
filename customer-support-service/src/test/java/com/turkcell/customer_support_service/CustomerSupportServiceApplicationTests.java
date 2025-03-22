package com.turkcell.customer_support_service;

import com.turkcell.customer_support_service.entity.CustomerSupport;
import com.turkcell.customer_support_service.repository.CustomerSupportRepository;
import com.turkcell.customer_support_service.rules.CustomerSupportBusinessRules;
import com.turkcell.customer_support_service.service.impl.CustomerSupportServiceImpl;
import io.github.bothuany.dtos.support.TicketCreateDTO;
import io.github.bothuany.dtos.support.TicketResponseDTO;
import io.github.bothuany.enums.IssueTypes;
import io.github.bothuany.enums.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerSupportServiceApplicationTests {

    @Mock
    private CustomerSupportRepository customerSupportRepository;

    @Mock
    private CustomerSupportBusinessRules customerSupportBusinessRules;

    @InjectMocks
    private CustomerSupportServiceImpl customerSupportService;

    private UUID customerId;
    private UUID ticketId;
    private CustomerSupport customerSupport;
    private TicketCreateDTO ticketCreateDTO;

    @BeforeEach
    public void setUp() {
        customerId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        customerSupport = new CustomerSupport(IssueTypes.BILLING, customerId, "Billing issue", TicketStatus.OPEN);
        ticketCreateDTO = new TicketCreateDTO(customerId, IssueTypes.BILLING, "Billing issue");
    }

    @Test
    public void testCreateTicket() {
        doNothing().when(customerSupportBusinessRules).checkIfCustomerExists(customerId);
        when(customerSupportRepository.save(any(CustomerSupport.class))).thenReturn(customerSupport);

        TicketResponseDTO response = customerSupportService.createTicket(ticketCreateDTO);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(TicketStatus.OPEN, response.getStatus());
    }

    @Test
    public void testUpdateTicket() {
        doNothing().when(customerSupportBusinessRules).checkIfTicketExists(ticketId);
        doNothing().when(customerSupportBusinessRules).checkIfCustomerExists(customerId);
        when(customerSupportRepository.findById(ticketId)).thenReturn(Optional.of(customerSupport));
        when(customerSupportRepository.save(any(CustomerSupport.class))).thenReturn(customerSupport);

        TicketResponseDTO response = customerSupportService.updateTicket(ticketId, ticketCreateDTO);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(TicketStatus.OPEN, response.getStatus());
    }

    @Test
    public void testGetTicketById() {
        doNothing().when(customerSupportBusinessRules).checkIfTicketExists(ticketId);
        when(customerSupportRepository.findById(ticketId)).thenReturn(Optional.of(customerSupport));

        TicketResponseDTO response = customerSupportService.getTicketById(ticketId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
    }

    @Test
    public void testDeleteTicket() {
        doNothing().when(customerSupportBusinessRules).checkIfTicketExists(ticketId);
        doNothing().when(customerSupportRepository).deleteById(ticketId);

        assertDoesNotThrow(() -> customerSupportService.deleteTicket(ticketId));
        verify(customerSupportRepository, times(1)).deleteById(ticketId);
    }
}
