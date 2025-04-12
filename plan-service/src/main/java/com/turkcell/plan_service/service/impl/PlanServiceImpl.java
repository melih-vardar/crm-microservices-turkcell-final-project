package com.turkcell.plan_service.service.impl;

import com.turkcell.plan_service.entity.Plan;
import com.turkcell.plan_service.repository.PlanRepository;
import com.turkcell.plan_service.service.PlanService;
import io.github.bothuany.dtos.plan.PlanCreateDTO;
import io.github.bothuany.dtos.plan.PlanResponseDTO;
import com.turkcell.plan_service.rules.PlanBusinessRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
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
        // hatalı
    @Override
    public PlanResponseDTO getPlanByName(String name) {
        Plan plan = planRepository.findByName(name);
        PlanResponseDTO planResponseDTO = new PlanResponseDTO();
        planResponseDTO.setId(plan.getId());
        planResponseDTO.setName(plan.getName());
        planResponseDTO.setDescription(plan.getDescription());
        planResponseDTO.setPrice(plan.getPrice());
        return planResponseDTO;
    }

    @Override
    public PlanResponseDTO getPlanWithDurationMonth(String name, int month) {
        Plan plan = planRepository.findByNameAndDurationInMonths(name,month);

        PlanResponseDTO planResponseDTO = new PlanResponseDTO();
        planResponseDTO.setId(plan.getId());
        planResponseDTO.setName(plan.getName());
        planResponseDTO.setDescription(plan.getDescription());
        planResponseDTO.setPrice(plan.getPrice());
        return planResponseDTO;
    }

    @Override
    public void deletePlan(UUID id) {
        planBusinessRules.checkIfPlanExists(id);

        planRepository.deleteById(id);
    }

    private void updatePlanFromRequest(Plan plan, PlanCreateDTO planCreateDTO) {
        plan.setDescription(planCreateDTO.getDescription());
        plan.setName(planCreateDTO.getName());
        plan.setPrice(planCreateDTO.getPrice());
        plan.setDurationInMonths(planCreateDTO.getDurationInMonths());
    }

    private PlanResponseDTO convertToResponseDTO(Plan plan) {
        PlanResponseDTO planResponseDTO = new PlanResponseDTO();
        planResponseDTO.setDescription(plan.getDescription());
        planResponseDTO.setName(plan.getName());
        planResponseDTO.setPrice(plan.getPrice());

        return planResponseDTO;
    }
}
