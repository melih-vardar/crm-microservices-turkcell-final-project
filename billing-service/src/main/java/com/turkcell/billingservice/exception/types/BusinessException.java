package com.turkcell.billingservice.exception.types;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
} 