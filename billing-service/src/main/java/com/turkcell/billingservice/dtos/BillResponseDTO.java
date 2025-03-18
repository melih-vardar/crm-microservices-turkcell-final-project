package com.turkcell.billingservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {
    private UUID id;
    private String customerId;
    private BigDecimal amount;
    private LocalDateTime dueDate;
    private boolean isPaid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 