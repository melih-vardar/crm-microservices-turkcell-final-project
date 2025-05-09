package com.turkcell.billingservice.dtos;

import com.turkcell.billingservice.entities.BillStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {
    UUID id;
    UUID contractId;
    String customerId;
    BigDecimal amount;
    LocalDateTime dueDate;
    String billStatus;
    LocalDateTime createdAt;
    private LocalDateTime paymentDate;
    private boolean paid;
} 