package com.turkcell.billingservice.controllers;

import com.turkcell.billingservice.dtos.*;
import com.turkcell.billingservice.services.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Billing Service", description = "Billing service API endpoints")
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/bills")
    @Operation(summary = "Create a new bill")
    public ResponseEntity<BillResponseDTO> createBill(@RequestBody BillCreateDTO billCreateDTO) {
        return ResponseEntity.ok(billingService.createBill(billCreateDTO));
    }

    @GetMapping("/bills/{id}")
    @Operation(summary = "Get bill by ID")
    public ResponseEntity<BillResponseDTO> getBillById(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.getBillById(id));
    }

    @GetMapping("/bills/unpaid")
    @Operation(summary = "Get unpaid bills")
    public ResponseEntity<List<BillResponseDTO>> getUnpaidBills() {
        return ResponseEntity.ok(billingService.getUnpaidBills());
    }

    @PostMapping("/payments")
    @Operation(summary = "Process a payment")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(billingService.processPayment(paymentDTO));
    }

    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.getPaymentById(id));
    }

    @GetMapping("/payments/methods")
    @Operation(summary = "Get available payment methods")
    public ResponseEntity<List<String>> getPaymentMethods() {
        return ResponseEntity.ok(billingService.getPaymentMethods());
    }
}