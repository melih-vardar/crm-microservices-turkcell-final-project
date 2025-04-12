package com.turkcell.billingservice.rules;

import com.turkcell.billingservice.exceptions.BusinessException;
import io.github.bothuany.dtos.contract.ContractDetailedResponseDTO;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingBusinessRules {

    // Customer validasyonu
    public void checkIfCustomerExists(CustomerResponseDTO customer) {
        if (customer == null) {
            throw new BusinessException("Customer not found");
        }
    }
    // Contract validasyonu
    public void checkIfContractIsActive(ContractDetailedResponseDTO contractDetailedResponseDTO) {
        if (contractDetailedResponseDTO == null) {
            throw new BusinessException("Contract not found");
        }
        if (!contractDetailedResponseDTO.isActive()) {
            throw new BusinessException("Contract is not active");
        }
    }
}