package com.turkcell.plan_service;

import com.turkcell.plan_service.entity.Plan;
import com.turkcell.plan_service.repository.PlanRepository;
import com.turkcell.plan_service.rules.PlanBusinessRules;
import com.turkcell.plan_service.service.impl.PlanServiceImpl;
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
    private PlanBusinessRules planBusinessRules;

    @InjectMocks
    private PlanServiceImpl planService;

    private UUID planId;
    private PlanCreateDTO planCreateDTO;
    private Plan plan;

    @BeforeEach
    public void setUp() {
        planId = UUID.randomUUID();
        planCreateDTO = new PlanCreateDTO("Premium Plan", "Full access to features", 99.99, 12);
        plan = new Plan(planId, "Premium Plan", "Full access to features", 99.99, 12);
    }

    @Test
    public void testCreatePlan() {
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        PlanResponseDTO response = planService.createPlan(planCreateDTO);

        assertNotNull(response);
        assertEquals(planCreateDTO.getName(), response.getName());
        assertEquals(planCreateDTO.getDescription(), response.getDescription());
        assertEquals(planCreateDTO.getPrice(), response.getPrice());
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    public void testGetPlanById() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        PlanResponseDTO response = planService.getPlanById(planId);

        assertNotNull(response);
        assertEquals(plan.getName(), response.getName());
        assertEquals(plan.getDescription(), response.getDescription());
        assertEquals(plan.getPrice(), response.getPrice());
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    public void testUpdatePlan() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        PlanCreateDTO updatedDTO = new PlanCreateDTO("Updated Plan", "Updated Description", 79.99, 6);
        PlanResponseDTO response = planService.updatePlan(planId, updatedDTO);

        assertNotNull(response);
        assertEquals(updatedDTO.getName(), response.getName());
        assertEquals(updatedDTO.getDescription(), response.getDescription());
        assertEquals(updatedDTO.getPrice(), response.getPrice());
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
