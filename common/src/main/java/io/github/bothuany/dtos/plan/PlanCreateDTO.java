package io.github.bothuany.dtos.plan;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCreateDTO {
    @NotNull(message = "Basic plan ID cannot be null")
    private UUID basicPlanId;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @DecimalMax(value = "10000.0", message = "Price cannot be greater than 10000")
    private double price;

    @NotNull(message = "Duration cannot be null")
    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 24, message = "Duration cannot be more than 24 months")
    private int durationInMonths;
}