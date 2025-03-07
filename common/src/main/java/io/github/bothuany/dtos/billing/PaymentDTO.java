package io.github.bothuany.dtos.billing;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    @NotNull(message = "Bill ID cannot be null")
    private UUID billId;

    @NotBlank(message = "Payment method cannot be empty")
    @Pattern(regexp = "^(CREDIT_CARD|DEBIT_CARD|BANK_TRANSFER)$", message = "Payment method must be one of: CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER")
    private String paymentMethod;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @DecimalMax(value = "100000.0", message = "Amount cannot be greater than 100000")
    private double amount;
}