package com.turkcell.billingservice.clients;

import com.turkcell.billingservice.dtos.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", path = "/api/customers")
public interface CustomerClient {
    @GetMapping("/{id}")
    CustomerDTO getCustomerById(@PathVariable String id);
} 