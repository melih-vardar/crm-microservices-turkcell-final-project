package com.turkcell.analyticsservice.controller;

import com.turkcell.analyticsservice.dto.dto.TicketAnalyticsDto;
import com.turkcell.analyticsservice.service.CustomerSupportBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics/customer-support-behavior")
@RequiredArgsConstructor
public class CustomerSupportController {

    private final CustomerSupportBehaviorService customerSupportBehaviorService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketAnalyticsDto>> getAllTicketAnalytics() {
        return ResponseEntity.ok(customerSupportBehaviorService.getAllTicketAnalytics());
    }
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketAnalyticsDto> getTicketAnalyticByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerSupportBehaviorService.findTicketAnalyticsById(customerId));
    }
}
