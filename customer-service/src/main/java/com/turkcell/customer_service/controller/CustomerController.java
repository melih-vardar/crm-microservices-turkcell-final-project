package com.turkcell.customer_service.controller;

import com.turkcell.customer_service.service.CustomerService;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.bothuany.dtos.customer.CustomerCreateDTO;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerCreateDTO request) {
        return new ResponseEntity<>(customerService.createCustomer(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable UUID id,
            @Valid @RequestBody CustomerCreateDTO request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/bills")
    public ResponseEntity<List<Object>> getCustomerBills(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerBills(id));
    }

    @GetMapping("/{id}/contracts")
    public ResponseEntity<List<Object>> getCustomerContracts(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerContracts(id));
    }
}
