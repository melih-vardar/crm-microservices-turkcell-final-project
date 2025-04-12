package com.turkcell.billingservice.controllers;

import com.turkcell.billingservice.dtos.*;
import com.turkcell.billingservice.services.BilingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Tag(name = "Billing Service", description = "Billing service API endpoints")
public class BillingController {

    private final BilingService billingService;

    @PostMapping("/{customerId}/{contractId}")
    @Operation(summary = "Create a new bill")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<BillResponseDTO> createBill(@PathVariable String customerId, @PathVariable String contractId) {
        BillResponseDTO createdBill = billingService.createBill(customerId, contractId);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    @GetMapping("/{billId}")
    @Operation(summary = "Get bill by ID")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<BillResponseDTO> getBillById(@PathVariable UUID billId) {
        return ResponseEntity.ok(billingService.getBillById(billId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<List<BillResponseDTO>> getBillsByCustomerId(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(billingService.getBillsByCustomerId(customerId));
    }
    @PutMapping("/{billId}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<BillResponseDTO> updateBill(
            @PathVariable UUID billId,
            @Valid @RequestBody BillUpdateDTO updateDTO) {

        return ResponseEntity.ok(billingService.updateBill(billId, updateDTO));
    }
    @DeleteMapping("/{billId}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<Void> deleteBill(
            @PathVariable UUID billId) {

        billingService.deleteBill(billId);
        return ResponseEntity.noContent().build();
    }
}