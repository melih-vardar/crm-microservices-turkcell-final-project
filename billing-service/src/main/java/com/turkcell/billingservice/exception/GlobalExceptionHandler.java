package com.turkcell.billingservice.exception;

import com.turkcell.billingservice.exception.result.BusinessExceptionResult;
import com.turkcell.billingservice.exception.result.ValidationExceptionResult;
import com.turkcell.billingservice.exception.types.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessExceptionResult handleBusinessException(BusinessException exception) {
        return new BusinessExceptionResult(exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResult handleValidationException(MethodArgumentNotValidException exception) {
        return new ValidationExceptionResult(
                exception.getBindingResult().getFieldErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .toList()
        );
    }
} 