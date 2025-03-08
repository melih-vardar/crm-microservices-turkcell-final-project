package com.turkcell.billingservice.controllers;

import io.github.bothuany.dtos.billing.BillCreateDTO;
import io.github.bothuany.dtos.billing.BillResponseDTO;
import io.github.bothuany.dtos.billing.PaymentDTO;
import com.turkcell.billingservice.entities.Payment;
import com.turkcell.billingservice.services.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;

    @PostMapping("/invoices")
    public ResponseEntity<BillResponseDTO> createInvoice(@RequestBody BillCreateDTO createDTO) {
        return ResponseEntity.ok(billingService.createInvoice(createDTO));
    }

    @GetMapping("/invoices/customer/{customerId}")
    public ResponseEntity<List<BillResponseDTO>> getCustomerInvoices(@PathVariable UUID customerId) {
        return ResponseEntity.ok(billingService.getCustomerInvoices(customerId));
    }

    @GetMapping("/invoices/customer/{customerId}/unpaid")
    public ResponseEntity<List<BillResponseDTO>> getUnpaidInvoices(@PathVariable UUID customerId) {
        return ResponseEntity.ok(billingService.getUnpaidInvoices(customerId));
    }

    @PostMapping("/payments")
    public ResponseEntity<BillResponseDTO> processPayment(@RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(billingService.processPayment(paymentDTO));
    }

    @GetMapping("/payments/invoice/{invoiceId}")
    public ResponseEntity<List<Payment>> getInvoicePayments(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(billingService.getInvoicePayments(invoiceId));
    }
}