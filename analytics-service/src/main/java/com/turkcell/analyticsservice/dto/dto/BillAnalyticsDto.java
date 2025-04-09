package com.turkcell.analyticsservice.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BillAnalyticsDto {
    UUID id;
    String customerId;
    BigDecimal amount;
    LocalDateTime dueDate;
    LocalDateTime createdAt;

}
