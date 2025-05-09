package com.turkcell.plan_service;

import com.turkcell.plan_service.entity.BasicPlan;
import com.turkcell.plan_service.entity.Plan;
import com.turkcell.plan_service.repository.BasicPlanRepository;
import com.turkcell.plan_service.repository.PlanRepository;
import com.turkcell.plan_service.rules.PlanBusinessRules;
import com.turkcell.plan_service.service.impl.PlanServiceImpl;
import io.github.bothuany.dtos.plan.BasicPlanResponseDTO;
import io.github.bothuany.dtos.plan.PlanCreateDTO;
import io.github.bothuany.dtos.plan.PlanResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanServiceApplicationTests {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private BasicPlanRepository basicPlanRepository;

    @Mock
    private PlanBusinessRules planBusinessRules;

    @InjectMocks
    private PlanServiceImpl planService;

    private UUID planId;
    private UUID basicPlanId;
    private PlanCreateDTO planCreateDTO;
    private Plan plan;
    private BasicPlan basicPlan;

    @BeforeEach
    public void setUp() {
        planId = UUID.randomUUID();
        basicPlanId = UUID.randomUUID();

        // Create BasicPlan
        basicPlan = new BasicPlan();
        basicPlan.setId(basicPlanId);
        basicPlan.setName("Premium Internet");
        basicPlan.setDescription("High-speed internet");

        // Create PlanCreateDTO
        planCreateDTO = new PlanCreateDTO();
        planCreateDTO.setBasicPlanId(basicPlanId);
        planCreateDTO.setPrice(99.99);
        planCreateDTO.setDurationInMonths(12);

        // Create Plan
        plan = new Plan();
        plan.setId(planId);
        plan.setBasicPlan(basicPlan);
        plan.setPrice(99.99);
        plan.setDurationInMonths(12);
    }

    @Test
    public void testCreatePlan() {
        when(basicPlanRepository.findById(basicPlanId)).thenReturn(Optional.of(basicPlan));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        PlanResponseDTO response = planService.createPlan(planCreateDTO);

        assertNotNull(response);
        assertEquals(basicPlan.getName(), response.getBasicPlan().getName());
        assertEquals(basicPlan.getDescription(), response.getBasicPlan().getDescription());
        assertEquals(planCreateDTO.getPrice(), response.getPrice());
        assertEquals(planCreateDTO.getDurationInMonths(), response.getDurationInMonths());
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    public void testGetPlanById() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        PlanResponseDTO response = planService.getPlanById(planId);

        assertNotNull(response);
        assertEquals(basicPlan.getName(), response.getBasicPlan().getName());
        assertEquals(basicPlan.getDescription(), response.getBasicPlan().getDescription());
        assertEquals(plan.getPrice(), response.getPrice());
        assertEquals(plan.getDurationInMonths(), response.getDurationInMonths());
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    public void testUpdatePlan() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(basicPlanRepository.findById(basicPlanId)).thenReturn(Optional.of(basicPlan));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        PlanCreateDTO updatedDTO = new PlanCreateDTO();
        updatedDTO.setBasicPlanId(basicPlanId);
        updatedDTO.setPrice(79.99);
        updatedDTO.setDurationInMonths(6);

        plan.setPrice(79.99);
        plan.setDurationInMonths(6);

        PlanResponseDTO response = planService.updatePlan(planId, updatedDTO);

        assertNotNull(response);
        assertEquals(basicPlan.getName(), response.getBasicPlan().getName());
        assertEquals(basicPlan.getDescription(), response.getBasicPlan().getDescription());
        assertEquals(updatedDTO.getPrice(), response.getPrice());
        assertEquals(updatedDTO.getDurationInMonths(), response.getDurationInMonths());
        verify(planRepository, times(1)).findById(planId);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    public void testDeletePlan() {
        doNothing().when(planBusinessRules).checkIfPlanExists(planId);

        planService.deletePlan(planId);

        verify(planRepository, times(1)).deleteById(planId);
    }
}
