package com.turkcell.customer_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "billingservice")
public interface BillingClient {
    @GetMapping("/api/bills/customer/{customerId}")
    List<Object> getCustomerBills(@PathVariable("customerId") UUID customerId);
}