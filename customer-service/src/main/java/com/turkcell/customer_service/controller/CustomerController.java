package com.turkcell.customer_service.controller;

import com.turkcell.customer_service.service.CustomerService;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.github.bothuany.dtos.customer.CustomerCreateDTO;

import java.util.UUID;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        log.info("Test endpoint accessed");
        return ResponseEntity.ok(Map.of("status", "success", "message", "Customer service is working"));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        logAuthenticationDetails("getAllCustomers");
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerCreateDTO request) {
        logAuthenticationDetails("createCustomer");
        return new ResponseEntity<>(customerService.createCustomer(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable UUID id) {
        logAuthenticationDetails("getCustomer");
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable UUID id,
            @Valid @RequestBody CustomerCreateDTO request) {
        logAuthenticationDetails("updateCustomer");
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        logAuthenticationDetails("deleteCustomer");
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/bills")
    public ResponseEntity<List<Object>> getCustomerBills(@PathVariable UUID id) {
        logAuthenticationDetails("getCustomerBills");
        return ResponseEntity.ok(customerService.getCustomerBills(id));
    }

    @GetMapping("/{id}/contracts")
    public ResponseEntity<List<Object>> getCustomerContracts(@PathVariable UUID id) {
        logAuthenticationDetails("getCustomerContracts");
        return ResponseEntity.ok(customerService.getCustomerContracts(id));
    }

    private void logAuthenticationDetails(String methodName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.debug("{} accessed by user: {}, authorities: {}",
                    methodName, auth.getName(), auth.getAuthorities());
        } else {
            log.debug("{} accessed with no authentication", methodName);
        }
    }
}
