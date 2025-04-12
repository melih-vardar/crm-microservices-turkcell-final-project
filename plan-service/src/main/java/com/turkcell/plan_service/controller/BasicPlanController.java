package com.turkcell.plan_service.controller;

import com.turkcell.plan_service.service.BasicPlanService;
import io.github.bothuany.dtos.plan.BasicPlanCreateDTO;
import io.github.bothuany.dtos.plan.BasicPlanResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/basic-plans")
@RequiredArgsConstructor
public class BasicPlanController {
    private final BasicPlanService basicPlanService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<BasicPlanResponseDTO> createBasicPlan(@RequestBody BasicPlanCreateDTO basicPlanCreateDTO) {
        return new ResponseEntity<>(basicPlanService.createBasicPlan(basicPlanCreateDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<BasicPlanResponseDTO> getBasicPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(basicPlanService.getBasicPlanById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<List<BasicPlanResponseDTO>> getAllBasicPlans() {
        return ResponseEntity.ok(basicPlanService.getAllBasicPlans());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<BasicPlanResponseDTO> updateBasicPlan(@PathVariable UUID id,
            @RequestBody BasicPlanCreateDTO basicPlanCreateDTO) {
        return ResponseEntity.ok(basicPlanService.updateBasicPlan(id, basicPlanCreateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBasicPlan(@PathVariable UUID id) {
        basicPlanService.deleteBasicPlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}