package com.turkcell.crmmicroservicesfinalproject.contractservice.client;

import io.github.bothuany.dtos.plan.PlanResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
//denenmedi değiştirilecek
@FeignClient(name = "plan-service")
public interface PlanClient {

    @GetMapping("/plans/api/plans/filter")
    PlanResponseDTO getPlanByNameAndMonth(@RequestParam("name") String name, @RequestParam("month") int month);
}
