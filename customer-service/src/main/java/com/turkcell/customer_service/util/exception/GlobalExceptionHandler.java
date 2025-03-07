package com.turkcell.customer_service.util.exception;

import com.turkcell.customer_service.util.exception.result.BusinessExceptionResult;
import com.turkcell.customer_service.util.exception.result.ValidationExceptionResult;
import com.turkcell.customer_service.util.exception.type.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ BusinessException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessExceptionResult handleRuntimeException(BusinessException e) {
        return new BusinessExceptionResult(e.getMessage());
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ValidationExceptionResult(e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map((error) -> error.getDefaultMessage())
                .toList());
    }
}