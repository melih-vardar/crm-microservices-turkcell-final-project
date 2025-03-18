package com.turkcell.billingservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillCreateDTO {
    private String customerId;
    private String contractId;
    private BigDecimal amount;
    private LocalDateTime dueDate;
} 