package com.turkcell.customer_support_service.service;

import io.github.bothuany.dtos.support.TicketCreateDTO;
import io.github.bothuany.dtos.support.TicketResponseDTO;
import com.turkcell.customer_support_service.entity.CustomerSupport;

import java.util.List;
import java.util.UUID;

public interface CustomerSupportService {
    TicketResponseDTO createTicket(TicketCreateDTO ticket);

    TicketResponseDTO updateTicket(UUID id, TicketCreateDTO ticket);

    List<TicketResponseDTO> getTicketsByCustomerId(UUID customerId);

    TicketResponseDTO getTicketById(UUID id);

    List<TicketResponseDTO> getAllTickets();

    void deleteTicket(UUID id);
}
