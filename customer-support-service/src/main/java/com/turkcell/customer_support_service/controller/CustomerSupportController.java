package com.turkcell.customer_support_service.controller;

import com.turkcell.customer_support_service.service.CustomerSupportService;
import io.github.bothuany.dtos.support.TicketCreateDTO;
import io.github.bothuany.dtos.support.TicketResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer-support")
@RequiredArgsConstructor
public class CustomerSupportController {
    private final CustomerSupportService customerSupportService;

    @PostMapping("/tickets")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketCreateDTO ticketCreateDTO) {
        return new ResponseEntity<>(customerSupportService.createTicket(ticketCreateDTO), HttpStatus.CREATED);
    }

    @GetMapping("/tickets/{id}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerSupportService.getTicketById(id));
    }

    @PutMapping("/tickets/{id}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<TicketResponseDTO> updateTicket(@PathVariable UUID id,
            @RequestBody TicketCreateDTO ticketCreateDTO) {
        return ResponseEntity.ok(customerSupportService.updateTicket(id, ticketCreateDTO));
    }

    @DeleteMapping("/tickets/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        customerSupportService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
