package com.turkcell.plan_service.service;

import io.github.bothuany.dtos.plan.PlanCreateDTO;
import io.github.bothuany.dtos.plan.PlanResponseDTO;

import java.util.UUID;

public interface PlanService {
    PlanResponseDTO createPlan(PlanCreateDTO planCreateDTO);

    PlanResponseDTO getPlanById(UUID id);

    PlanResponseDTO updatePlan(UUID id, PlanCreateDTO planCreateDTO);

    void deletePlan(UUID id);
}
