package com.turkcell.customer_service.service;

import io.github.bothuany.dtos.customer.CustomerCreateDTO;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerCreateDTO request);

    CustomerResponseDTO getCustomer(UUID id);

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO updateCustomer(UUID id, CustomerCreateDTO request);

    void deleteCustomer(UUID id);

    List<Object> getCustomerBills(UUID customerId);

    List<Object> getCustomerContracts(UUID customerId);
}
