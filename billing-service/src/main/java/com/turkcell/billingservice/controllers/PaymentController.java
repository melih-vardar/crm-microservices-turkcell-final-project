package com.turkcell.billingservice.controllers;

import com.turkcell.billingservice.dtos.PaymentRequest;
import com.turkcell.billingservice.dtos.PaymentResponseDTO;
import com.turkcell.billingservice.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{billId}")
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @PathVariable UUID billId,
            @Valid @RequestBody PaymentRequest paymentRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.processPayment(billId, paymentRequest));
    }

    // ID ile Ödeme Getir
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
