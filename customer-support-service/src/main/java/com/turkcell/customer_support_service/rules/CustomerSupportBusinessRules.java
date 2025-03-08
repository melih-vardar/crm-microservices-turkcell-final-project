package com.turkcell.customer_support_service.rules;

import com.turkcell.customer_support_service.repository.CustomerSupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerSupportBusinessRules {

    private final CustomerSupportRepository customerSupportRepository;

    public void checkIfTicketExists(UUID ticketId) {
        if(!customerSupportRepository.findById(ticketId)) {
            throw new BusinessException("Ticket not found.");
        }
    }
}
