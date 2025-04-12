package com.turkcell.billingservice.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
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
public class BillUpdateDTO {

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
}
