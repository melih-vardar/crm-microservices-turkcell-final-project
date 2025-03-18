package com.turkcell.billingservice.clients;

import com.turkcell.billingservice.dtos.PaymentRequestDTO;
import com.turkcell.billingservice.dtos.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", path = "/api/v1/payments")
public interface PaymentClient {
    @PostMapping
    PaymentResponseDTO processPayment(@RequestBody PaymentRequestDTO request);
} 