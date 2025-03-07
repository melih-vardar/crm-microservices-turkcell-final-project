package com.turkcell.customer_service.util.exception.type;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}