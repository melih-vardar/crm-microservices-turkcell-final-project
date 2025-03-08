package com.turkcell.customer_support_service.util.exception.result;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessExceptionResult extends ExceptionResult {
    private String message;

    public BusinessExceptionResult(String message) {
        super("Business Exception");
        this.message = message;
    }
}
