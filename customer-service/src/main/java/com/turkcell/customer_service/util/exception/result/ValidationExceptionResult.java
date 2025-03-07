package com.turkcell.customer_service.util.exception.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidationExceptionResult extends ExceptionResult {
    private List<String> errors;

    public ValidationExceptionResult(List<String> errors) {
        super("ValidationError");
        this.errors = errors;
    }
}