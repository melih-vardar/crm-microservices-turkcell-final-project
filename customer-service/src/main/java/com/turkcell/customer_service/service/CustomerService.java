package com.turkcell.customer_service.service;

import io.github.bothuany.dtos.customer.CustomerCreateDTO;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;

import java.util.UUID;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerCreateDTO request);

    CustomerResponseDTO getCustomer(UUID id);

    CustomerResponseDTO updateCustomer(UUID id, CustomerCreateDTO request);

    void deleteCustomer(UUID id);
}
