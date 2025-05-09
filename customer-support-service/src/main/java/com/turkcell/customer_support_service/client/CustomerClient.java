package com.turkcell.customer_support_service.client;

import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name="customerservice")
public interface CustomerClient {
    @GetMapping("/api/customers/{uuid}")
    CustomerResponseDTO getCustomer(@PathVariable("uuid") UUID uuid);
}
