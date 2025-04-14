package com.turkcell.customer_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "contractservice")
public interface ContractClient {
    @GetMapping("/api/contracts/customer/{customerId}")
    List<Object> getCustomerContracts(@PathVariable("customerId") UUID customerId);
}