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
@RequestMapping("/api/billings")
@RequiredArgsConstructor
@Tag(name = "Billing Service", description = "Billing service API endpoints")
public class BillingController {

    private final BilingService billingService;

    @PostMapping("/{customerId}/{contractId}")
    @Operation(summary = "Create a new bill")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<BillResponseDTO> createBill(@PathVariable String customerId,
            @PathVariable String contractId) {
        BillResponseDTO createdBill = billingService.createBill(customerId, contractId);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    @GetMapping("/{billId}")
    @Operation(summary = "Get bill by ID")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<BillResponseDTO> getBillById(@PathVariable UUID billId) {
        return ResponseEntity.ok(billingService.getBillById(billId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<List<BillResponseDTO>> getBillsByCustomerId(@PathVariable String customerId) {

        return ResponseEntity.ok(billingService.getBillsByCustomerId(customerId));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    @Operation(summary = "Get all bills")
    public ResponseEntity<List<BillResponseDTO>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @PutMapping("/{billId}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<BillResponseDTO> updateBill(@PathVariable UUID billId,
            @Valid @RequestBody BillUpdateDTO updateDTO) {

        return ResponseEntity.ok(billingService.updateBill(billId, updateDTO));
    }

    @DeleteMapping("/{billId}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBill(@PathVariable UUID billId) {

        billingService.deleteBill(billId);
        return ResponseEntity.noContent().build();
    }
}