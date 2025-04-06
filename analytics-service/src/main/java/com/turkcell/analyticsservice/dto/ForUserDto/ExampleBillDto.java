package com.turkcell.analyticsservice.dto.ForUserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExampleBillDto {
    String customerId;
    BigDecimal amount;
    LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
