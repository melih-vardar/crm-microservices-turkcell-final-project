package com.turkcell.plan_service.controller;

import com.turkcell.plan_service.service.PlanService;
import io.github.bothuany.dtos.plan.PlanCreateDTO;
import io.github.bothuany.dtos.plan.PlanResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanResponseDTO> createPlan(@RequestBody PlanCreateDTO planCreateDTO) {
        return new ResponseEntity<>(planService.createPlan(planCreateDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> getPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> updatePlan(@PathVariable UUID id, @RequestBody PlanCreateDTO planCreateDTO) {
        return ResponseEntity.ok(planService.updatePlan(id, planCreateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> deletePlan(@PathVariable UUID id) {
        planService.deletePlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
