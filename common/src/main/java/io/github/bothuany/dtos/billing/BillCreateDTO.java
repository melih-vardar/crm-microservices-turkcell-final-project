package io.github.bothuany.dtos.billing;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillCreateDTO {
    @NotNull(message = "Customer ID cannot be null")
    private UUID customerId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @DecimalMax(value = "100000.0", message = "Amount cannot be greater than 100000")
    private double amount;

    @NotNull(message = "Due date cannot be null")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Due date must be in format YYYY-MM-DD")
    private String dueDate;
}