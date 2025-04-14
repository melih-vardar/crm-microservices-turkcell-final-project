package com.turkcell.customer_support_service;

import com.turkcell.customer_support_service.entity.CustomerSupport;
import com.turkcell.customer_support_service.repository.CustomerSupportRepository;
import com.turkcell.customer_support_service.rules.CustomerSupportBusinessRules;
import com.turkcell.customer_support_service.service.impl.CustomerSupportServiceImpl;
import com.turkcell.customer_support_service.util.exception.type.BusinessException;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private static final String TEST_DESCRIPTION = "Test ticket description";

    @BeforeEach
    public void setUp() {
        customerId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        customerSupport = new CustomerSupport(IssueTypes.BILLING, customerId, TEST_DESCRIPTION, TicketStatus.OPEN);
        customerSupport.setId(ticketId);
        ticketCreateDTO = new TicketCreateDTO(customerId, IssueTypes.BILLING, TEST_DESCRIPTION);
    }

    @Test
    public void testCreateTicket_Success() {
        doNothing().when(customerSupportBusinessRules).checkIfCustomerExists(customerId);
        when(customerSupportRepository.save(any(CustomerSupport.class))).thenReturn(customerSupport);

        TicketResponseDTO response = customerSupportService.createTicket(ticketCreateDTO);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(TicketStatus.OPEN, response.getStatus());
        assertEquals(IssueTypes.BILLING, response.getIssueType());
        
        verify(customerSupportBusinessRules).checkIfCustomerExists(customerId);
        verify(customerSupportRepository).save(any(CustomerSupport.class));
    }

    @Test
    public void testCreateTicket_CustomerNotFound() {
        doThrow(new BusinessException("Customer not found"))
            .when(customerSupportBusinessRules).checkIfCustomerExists(customerId);

        assertThrows(BusinessException.class, () -> 
            customerSupportService.createTicket(ticketCreateDTO));
        
        verify(customerSupportRepository, never()).save(any(CustomerSupport.class));
    }

    @Test
    public void testUpdateTicket_Success() {
        doNothing().when(customerSupportBusinessRules).checkIfTicketExists(ticketId);
        doNothing().when(customerSupportBusinessRules).checkIfCustomerExists(customerId);
        when(customerSupportRepository.findById(ticketId)).thenReturn(Optional.of(customerSupport));
        when(customerSupportRepository.save(any(CustomerSupport.class))).thenReturn(customerSupport);

        TicketResponseDTO response = customerSupportService.updateTicket(ticketId, ticketCreateDTO);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(TicketStatus.OPEN, response.getStatus());
        assertEquals(IssueTypes.BILLING, response.getIssueType());
        
        verify(customerSupportRepository).save(any(CustomerSupport.class));
    }

    @Test
    public void testUpdateTicket_TicketNotFound() {
        doThrow(new BusinessException("Ticket not found."))
            .when(customerSupportBusinessRules).checkIfTicketExists(ticketId);

        assertThrows(BusinessException.class, () -> 
            customerSupportService.updateTicket(ticketId, ticketCreateDTO));
        
        verify(customerSupportRepository, never()).save(any(CustomerSupport.class));
    }

    @Test
    public void testUpdateTicket_CustomerNotFound() {
        doNothing().when(customerSupportBusinessRules).checkIfTicketExists(ticketId);
        doThrow(new BusinessException("Customer not found"))
            .when(customerSupportBusinessRules).checkIfCustomerExists(customerId);

        assertThrows(BusinessException.class, () -> 
            customerSupportService.updateTicket(ticketId, ticketCreateDTO));
        
        verify(customerSupportRepository, never()).save(any(CustomerSupport.class));
    }

    @Test
    public void testGetTicketById_Success() {
        doNothing().when(customerSupportBusinessRules).checkIfTicketExists(ticketId);
        when(customerSupportRepository.findById(ticketId)).thenReturn(Optional.of(customerSupport));

        TicketResponseDTO response = customerSupportService.getTicketById(ticketId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(IssueTypes.BILLING, response.getIssueType());
        assertEquals(TicketStatus.OPEN, response.getStatus());
    }

    @Test
    public void testGetTicketById_NotFound() {
        doThrow(new BusinessException("Ticket not found."))
            .when(customerSupportBusinessRules).checkIfTicketExists(ticketId);

        assertThrows(BusinessException.class, () -> 
            customerSupportService.getTicketById(ticketId));
    }

    @Test
    public void testGetTicketsByCustomerId_Success() {
        doNothing().when(customerSupportBusinessRules).checkIfCustomerExists(customerId);
        List<TicketResponseDTO> tickets = Collections.singletonList(new TicketResponseDTO(ticketId, customerId, IssueTypes.BILLING, TicketStatus.OPEN));
        when(customerSupportRepository.getAllByCustomerId(customerId)).thenReturn(tickets);

        List<TicketResponseDTO> responses = customerSupportService.getTicketsByCustomerId(customerId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(customerId, responses.getFirst().getCustomerId());
    }

    @Test
    public void testGetTicketsByCustomerId_CustomerNotFound() {
        doThrow(new BusinessException("Customer not found"))
            .when(customerSupportBusinessRules).checkIfCustomerExists(customerId);

        assertThrows(BusinessException.class, () -> 
            customerSupportService.getTicketsByCustomerId(customerId));
        
        verify(customerSupportRepository, never()).getAllByCustomerId(any());
    }

    @Test
    public void testGetAllTickets_Success() {
        List<CustomerSupport> tickets = Collections.singletonList(customerSupport);
        when(customerSupportRepository.findAll()).thenReturn(tickets);

        List<TicketResponseDTO> responses = customerSupportService.getAllTickets();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(customerId, responses.getFirst().getCustomerId());
    }

    @Test
    public void testGetAllTickets_Empty() {
        when(customerSupportRepository.findAll()).thenReturn(Collections.emptyList());

        List<TicketResponseDTO> responses = customerSupportService.getAllTickets();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void testDeleteTicket_Success() {
        doNothing().when(customerSupportBusinessRules).checkIfTicketExists(ticketId);
        when(customerSupportRepository.findById(ticketId)).thenReturn(Optional.of(customerSupport));
        doNothing().when(customerSupportRepository).deleteById(ticketId);

        assertDoesNotThrow(() -> customerSupportService.deleteTicket(ticketId));
        
        verify(customerSupportRepository).deleteById(ticketId);
    }

    @Test
    public void testDeleteTicket_NotFound() {
        doThrow(new BusinessException("Ticket not found."))
            .when(customerSupportBusinessRules).checkIfTicketExists(ticketId);

        assertThrows(BusinessException.class, () -> 
            customerSupportService.deleteTicket(ticketId));
        
        verify(customerSupportRepository, never()).deleteById(any());
    }
}
