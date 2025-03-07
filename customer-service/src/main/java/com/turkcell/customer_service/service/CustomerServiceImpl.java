package com.turkcell.customer_service.service;

import com.turkcell.customer_service.entity.Customer;
import com.turkcell.customer_service.repository.CustomerRepository;
import com.turkcell.customer_service.rules.CustomerBusinessRules;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import io.github.bothuany.dtos.customer.CustomerCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerBusinessRules customerBusinessRules;

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerCreateDTO request) {
        customerBusinessRules.checkIfEmailExists(request.getEmail());
        customerBusinessRules.checkIfPhoneExists(request.getPhone());

        Customer customer = new Customer();
        updateCustomerFromRequest(customer, request);
        return convertToResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO getCustomer(UUID id) {
        customerBusinessRules.checkIfCustomerExists(id);
        return convertToResponse(customerRepository.findById(id).get());
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(UUID id, CustomerCreateDTO request) {
        customerBusinessRules.checkIfCustomerExists(id);
        customerBusinessRules.checkIfEmailExistsForUpdate(id, request.getEmail());
        customerBusinessRules.checkIfPhoneExistsForUpdate(id, request.getPhone());

        Customer customer = customerRepository.findById(id).get();
        updateCustomerFromRequest(customer, request);
        return convertToResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id) {
        customerBusinessRules.checkIfCustomerExists(id);
        customerRepository.deleteById(id);
    }

    private void updateCustomerFromRequest(Customer customer, CustomerCreateDTO request) {
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
    }

    private CustomerResponseDTO convertToResponse(Customer customer) {
        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        return response;
    }
}