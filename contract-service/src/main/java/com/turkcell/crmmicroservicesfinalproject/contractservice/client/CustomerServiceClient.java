package com.turkcell.crmmicroservicesfinalproject.contractservice.client;

import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customerservice", path = "/api/customers")
public interface CustomerServiceClient {

    @GetMapping("/{id}")
    CustomerResponseDTO getCustomerById(@PathVariable UUID id);
}