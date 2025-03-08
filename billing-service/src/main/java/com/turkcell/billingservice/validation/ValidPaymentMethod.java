package com.turkcell.billingservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PaymentMethodValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPaymentMethod {
    String message() default "Geçersiz ödeme yöntemi";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 