package com.turkcell.billingservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillCreatedEvent {
    private UUID billId;
    private String customerId;
    private BigDecimal amount;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
} 