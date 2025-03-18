package com.turkcell.plan_service.rules;

import com.turkcell.plan_service.entity.Plan;
import com.turkcell.plan_service.exception.type.BusinessException;
import com.turkcell.plan_service.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanBusinessRules {
    private final PlanRepository planRepository;

    public void checkIfPlanExists(UUID planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("Plan not found"));
    }
}
