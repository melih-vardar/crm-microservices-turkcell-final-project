package com.turkcell.crmmicroservicesfinalproject.contractservice.client;

import io.github.bothuany.dtos.plan.PlanResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "planservice", path = "/api/plans")
public interface PlanServiceClient {

    @GetMapping("/{id}")
    PlanResponseDTO getPlanById(@PathVariable UUID id);
}