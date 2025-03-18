package com.turkcell.billingservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private String billId;
    private BigDecimal amount;
    private String paymentMethod;
    private String customerId;
} 