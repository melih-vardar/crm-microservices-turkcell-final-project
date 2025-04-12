package com.turkcell.billingservice.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private UUID id;
    private UUID billId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
} 