package com.turkcell.plan_service.service;

import io.github.bothuany.dtos.plan.BasicPlanCreateDTO;
import io.github.bothuany.dtos.plan.BasicPlanResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BasicPlanService {
    BasicPlanResponseDTO createBasicPlan(BasicPlanCreateDTO basicPlanCreateDTO);

    BasicPlanResponseDTO getBasicPlanById(UUID id);

    List<BasicPlanResponseDTO> getAllBasicPlans();

    BasicPlanResponseDTO updateBasicPlan(UUID id, BasicPlanCreateDTO basicPlanCreateDTO);

    void deleteBasicPlan(UUID id);
}