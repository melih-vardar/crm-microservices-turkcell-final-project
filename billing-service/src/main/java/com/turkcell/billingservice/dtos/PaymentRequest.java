package com.turkcell.billingservice.dtos;

import com.turkcell.billingservice.entities.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "Ödeme yöntemi boş olamaz")
    private PaymentMethod paymentMethod; // CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER

    @NotBlank(message = "Kart numarası boş olamaz")
    @Pattern(regexp = "^[0-9]{16}$", message = "Geçersiz kart numarası")
    private String cardNumber;

    @NotBlank(message = "Kart sahibi adı boş olamaz")
    private String cardHolderName;

    @NotBlank(message = "Son kullanma tarihi boş olamaz")
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/([0-9]{2})$", message = "MM/YY formatında olmalıdır")
    private String expiryDate;

    @NotBlank(message = "CVV boş olamaz")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "Geçersiz CVV")
    private String cvv;
}
