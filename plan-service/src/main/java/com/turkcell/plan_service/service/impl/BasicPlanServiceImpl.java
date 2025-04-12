package com.turkcell.plan_service.service.impl;

import com.turkcell.plan_service.entity.BasicPlan;
import com.turkcell.plan_service.repository.BasicPlanRepository;
import com.turkcell.plan_service.service.BasicPlanService;
import io.github.bothuany.dtos.plan.BasicPlanCreateDTO;
import io.github.bothuany.dtos.plan.BasicPlanResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicPlanServiceImpl implements BasicPlanService {
    private final BasicPlanRepository basicPlanRepository;

    @Override
    public BasicPlanResponseDTO createBasicPlan(BasicPlanCreateDTO basicPlanCreateDTO) {
        BasicPlan basicPlan = new BasicPlan();
        updateBasicPlanFromRequest(basicPlan, basicPlanCreateDTO);

        return convertToResponseDTO(basicPlanRepository.save(basicPlan));
    }

    @Override
    public BasicPlanResponseDTO getBasicPlanById(UUID id) {
        return convertToResponseDTO(basicPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Basic plan not found")));
    }

    @Override
    public List<BasicPlanResponseDTO> getAllBasicPlans() {
        return basicPlanRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BasicPlanResponseDTO updateBasicPlan(UUID id, BasicPlanCreateDTO basicPlanCreateDTO) {
        BasicPlan basicPlan = basicPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Basic plan not found"));

        updateBasicPlanFromRequest(basicPlan, basicPlanCreateDTO);

        return convertToResponseDTO(basicPlanRepository.save(basicPlan));
    }

    @Override
    public void deleteBasicPlan(UUID id) {
        basicPlanRepository.deleteById(id);
    }

    private void updateBasicPlanFromRequest(BasicPlan basicPlan, BasicPlanCreateDTO basicPlanCreateDTO) {
        basicPlan.setDescription(basicPlanCreateDTO.getDescription());
        basicPlan.setName(basicPlanCreateDTO.getName());
        basicPlan.setPlanType(basicPlanCreateDTO.getPlanType());
    }

    private BasicPlanResponseDTO convertToResponseDTO(BasicPlan basicPlan) {
        BasicPlanResponseDTO basicPlanResponseDTO = new BasicPlanResponseDTO();
        basicPlanResponseDTO.setId(basicPlan.getId());
        basicPlanResponseDTO.setDescription(basicPlan.getDescription());
        basicPlanResponseDTO.setName(basicPlan.getName());
        basicPlanResponseDTO.setPlanType(basicPlan.getPlanType());

        return basicPlanResponseDTO;
    }
}