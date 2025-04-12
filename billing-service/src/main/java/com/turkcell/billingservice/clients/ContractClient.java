package com.turkcell.billingservice.clients;

import io.github.bothuany.dtos.contract.ContractDetailedResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "contractservice")
public interface ContractClient {
    @GetMapping("/{id}/detailed")
    ContractDetailedResponseDTO getContractDetailed(@PathVariable UUID id);
} 