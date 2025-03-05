package io.github.bothuany.dtos.billing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private UUID billId;
    private String paymentMethod;
    private double amount;
}