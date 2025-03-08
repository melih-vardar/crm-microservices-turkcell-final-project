package io.github.bothuany.dtos.contract;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractCreateDTO {
    @NotNull(message = "Customer ID cannot be null")
    private UUID customerId;

    @NotNull(message = "Plan type cannot be null")
    private String planType;

    @NotNull(message = "Start date cannot be null")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in format YYYY-MM-DD")
    private String startDate;

    @NotNull(message = "End date cannot be null")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in format YYYY-MM-DD")
    private String endDate;
}