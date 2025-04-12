package com.turkcell.plan_service.service.impl;

import com.turkcell.plan_service.entity.BasicPlan;
import com.turkcell.plan_service.entity.Plan;
import com.turkcell.plan_service.repository.BasicPlanRepository;
import com.turkcell.plan_service.repository.PlanRepository;
import com.turkcell.plan_service.service.PlanService;
import io.github.bothuany.dtos.plan.BasicPlanResponseDTO;
import io.github.bothuany.dtos.plan.PlanCreateDTO;
import io.github.bothuany.dtos.plan.PlanResponseDTO;
import com.turkcell.plan_service.rules.PlanBusinessRules;
import io.github.bothuany.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final BasicPlanRepository basicPlanRepository;
    private final PlanBusinessRules planBusinessRules;

    @Override
    public PlanResponseDTO createPlan(PlanCreateDTO planCreateDTO) {
        Plan plan = new Plan();
        updatePlanFromRequest(plan, planCreateDTO);

        return convertToResponseDTO(planRepository.save(plan));
    }

    @Override
    public PlanResponseDTO getPlanById(UUID id) {
        planBusinessRules.checkIfPlanExists(id);

        return convertToResponseDTO(planRepository.findById(id).get());
    }

    @Override
    public PlanResponseDTO updatePlan(UUID id, PlanCreateDTO planCreateDTO) {
        planBusinessRules.checkIfPlanExists(id);

        Plan plan = planRepository.findById(id).get();
        updatePlanFromRequest(plan, planCreateDTO);

        return convertToResponseDTO(planRepository.save(plan));
    }

    @Override
    public PlanResponseDTO getPlanByName(String name) {
        Plan plan = planRepository.findByBasicPlanName(name);
        if (plan == null) {
            throw new BusinessException("Plan not found with name: " + name);
        }
        return convertToResponseDTO(plan);
    }

    @Override
    public PlanResponseDTO getPlanWithDurationMonth(String name, int month) {
        Plan plan = planRepository.findByBasicPlanNameAndDurationInMonths(name, month);
        if (plan == null) {
            throw new BusinessException("Plan not found with name: " + name + " and duration: " + month + " months");
        }
        return convertToResponseDTO(plan);
    }

    @Override
    public void deletePlan(UUID id) {
        planBusinessRules.checkIfPlanExists(id);

        planRepository.deleteById(id);
    }

    private void updatePlanFromRequest(Plan plan, PlanCreateDTO planCreateDTO) {
        BasicPlan basicPlan = basicPlanRepository.findById(planCreateDTO.getBasicPlanId())
                .orElseThrow(() -> new RuntimeException("Basic plan not found"));

        plan.setBasicPlan(basicPlan);
        plan.setPrice(planCreateDTO.getPrice());
        plan.setDurationInMonths(planCreateDTO.getDurationInMonths());
    }

    private PlanResponseDTO convertToResponseDTO(Plan plan) {
        PlanResponseDTO planResponseDTO = new PlanResponseDTO();
        planResponseDTO.setId(plan.getId());
        planResponseDTO.setPrice(plan.getPrice());
        planResponseDTO.setDurationInMonths(plan.getDurationInMonths());

        BasicPlanResponseDTO basicPlanResponseDTO = new BasicPlanResponseDTO();
        basicPlanResponseDTO.setId(plan.getBasicPlan().getId());
        basicPlanResponseDTO.setName(plan.getBasicPlan().getName());
        basicPlanResponseDTO.setDescription(plan.getBasicPlan().getDescription());
        basicPlanResponseDTO.setPlanType(plan.getBasicPlan().getPlanType());

        planResponseDTO.setBasicPlan(basicPlanResponseDTO);

        return planResponseDTO;
    }
}
