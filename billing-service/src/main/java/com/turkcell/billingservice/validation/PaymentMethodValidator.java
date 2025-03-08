package com.turkcell.billingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentMethodValidator implements ConstraintValidator<ValidPaymentMethod, String> {
    private static final String[] VALID_METHODS = {"CREDIT_CARD", "BANK_TRANSFER", "ONLINE_PAYMENT"};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        
        for (String method : VALID_METHODS) {
            if (method.equals(value)) return true;
        }
        return false;
    }
} 