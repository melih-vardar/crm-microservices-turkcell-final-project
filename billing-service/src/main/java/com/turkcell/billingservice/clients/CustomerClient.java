package com.turkcell.billingservice.clients;

import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customerservice")
public interface CustomerClient {
    @GetMapping("/{id}")
    CustomerResponseDTO getCustomerById(@PathVariable String id);
} 