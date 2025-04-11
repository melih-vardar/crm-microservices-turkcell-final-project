package com.turkcell.analyticsservice.controller;

import com.turkcell.analyticsservice.dto.dto.BillAnalyticsDto;
import com.turkcell.analyticsservice.service.BillBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/bill-behavior")
@RequiredArgsConstructor
public class BillBehaviorController {

    private final BillBehaviorService billBehaviorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BillAnalyticsDto>> getAllBillAnalytics() {
        return ResponseEntity.ok(billBehaviorService.getAllBillAnalytics());
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillAnalyticsDto> getBillAnalyticByCustomerId(
            @PathVariable String customerId) {
        return ResponseEntity.ok(billBehaviorService.getBillAnalyticsById(customerId));
    }
}
