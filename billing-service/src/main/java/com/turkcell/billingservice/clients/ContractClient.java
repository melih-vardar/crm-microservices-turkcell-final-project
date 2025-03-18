package com.turkcell.billingservice.clients;

import com.turkcell.billingservice.dtos.ContractDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "contract-service", path = "/api/v1/contracts")
public interface ContractClient {
    @GetMapping("/{id}")
    ContractDTO getContractById(@PathVariable String id);
} 