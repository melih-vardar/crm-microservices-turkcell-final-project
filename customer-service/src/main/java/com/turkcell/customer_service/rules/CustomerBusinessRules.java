package com.turkcell.customer_service.rules;

import com.turkcell.customer_service.repository.CustomerRepository;
import com.turkcell.customer_service.util.exception.type.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerBusinessRules {
    private final CustomerRepository customerRepository;

    public void checkIfCustomerExists(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new BusinessException("Customer not found with id: " + id);
        }
    }

    public void checkIfEmailExists(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new BusinessException("Email already exists: " + email);
        }
    }

    public void checkIfPhoneExists(String phone) {
        if (customerRepository.existsByPhone(phone)) {
            throw new BusinessException("Phone number already exists: " + phone);
        }
    }

    public void checkIfEmailExistsForUpdate(UUID id, String email) {
        if (!customerRepository.findById(id).get().getEmail().equals(email)
                && customerRepository.existsByEmail(email)) {
            throw new BusinessException("Email already exists: " + email);
        }
    }

    public void checkIfPhoneExistsForUpdate(UUID id, String phone) {
        if (!customerRepository.findById(id).get().getPhone().equals(phone)
                && customerRepository.existsByPhone(phone)) {
            throw new BusinessException("Phone number already exists: " + phone);
        }
    }
}