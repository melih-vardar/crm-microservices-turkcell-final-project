package com.turkcell.crmmicroservicesfinalproject.contractservice.client;

import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/customers/{id}")
    CustomerResponseDTO getCustomer(@PathVariable("id") UUID id);
}