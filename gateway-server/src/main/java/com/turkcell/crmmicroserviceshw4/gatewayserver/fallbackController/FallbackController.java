package com.turkcell.crmmicroserviceshw4.gatewayserver.fallbackController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public ResponseEntity<String> userServiceFallback() {
        return ResponseEntity.ok("User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/customers")
    public ResponseEntity<String> customerServiceFallback() {
        return ResponseEntity.ok("Customer Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/contracts")
    public ResponseEntity<String> contractServiceFallback() {
        return ResponseEntity.ok("Contract Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/plans")
    public ResponseEntity<String> planServiceFallback() {
        return ResponseEntity.ok("Plan Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/billings")
    public ResponseEntity<String> billingServiceFallback() {
        return ResponseEntity.ok("Billing Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/support")
    public ResponseEntity<String> supportServiceFallback() {
        return ResponseEntity.ok("Support Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/analytics")
    public ResponseEntity<String> analyticsServiceFallback() {
        return ResponseEntity.ok("Analytics Service is currently unavailable. Please try again later.");
    }
}

