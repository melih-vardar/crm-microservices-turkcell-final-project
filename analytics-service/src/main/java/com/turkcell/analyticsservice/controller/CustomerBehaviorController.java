package com.turkcell.analyticsservice.controller;

import com.turkcell.analyticsservice.dto.dto.CustomerAnalyticDto;
import com.turkcell.analyticsservice.service.CustomerBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics/customer-behavior")
@RequiredArgsConstructor
public class CustomerBehaviorController {

    private final CustomerBehaviorService customerBehaviorService;

    @GetMapping
    public ResponseEntity<List<CustomerAnalyticDto>> getAllCustomerBehaviors() {
        return ResponseEntity.ok(customerBehaviorService.getAllCustomerBehaviors());
    }
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerAnalyticDto> getCustomerAnalytic(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerBehaviorService.getCustomerAnalytic(customerId));
    }
}
