package com.turkcell.customer_support_service.rules;

import com.turkcell.customer_support_service.client.CustomerClient;
import com.turkcell.customer_support_service.entity.CustomerSupport;
import com.turkcell.customer_support_service.repository.CustomerSupportRepository;
import com.turkcell.customer_support_service.util.exception.type.BusinessException;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerSupportBusinessRules {

    private final CustomerSupportRepository customerSupportRepository;
    private final CustomerClient customerClient;

    public void checkIfTicketExists(UUID ticketId) {
        CustomerSupport customerSupport = customerSupportRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException("Ticket not found."));
    }

    public void checkIfCustomerExists(UUID customerId) {
        CustomerResponseDTO customerResponseDTO = customerClient.getCustomer(customerId);

        if(customerResponseDTO == null) {
            throw new BusinessException("Customer not found.");
        }
    }
}
