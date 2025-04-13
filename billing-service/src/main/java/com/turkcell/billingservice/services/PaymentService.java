package com.turkcell.billingservice.services;

import com.turkcell.billingservice.dtos.PaymentRequest;
import com.turkcell.billingservice.dtos.PaymentResponseDTO;
import com.turkcell.billingservice.entities.Bill;
import com.turkcell.billingservice.entities.PaymentMethod;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDTO processPayment(UUID billId, PaymentRequest paymentRequest);
    PaymentResponseDTO getPaymentById(UUID paymentId);
    List<PaymentResponseDTO> getAllPayments();
    PaymentResponseDTO getPaymentByBillId(UUID billId);
    List<PaymentResponseDTO> getPaymentsByStatus(boolean isSuccess);
    List<PaymentResponseDTO> getPaymentsByMethod(PaymentMethod method);
}
