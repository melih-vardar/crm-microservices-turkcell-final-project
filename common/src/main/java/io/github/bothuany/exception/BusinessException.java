package io.github.bothuany.exception;

/**
 * Represents business rule violations in the application.
 * This exception is thrown when a business rule is violated
 * and should be handled accordingly.
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructs a new business exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new business exception with the specified detail message and
     * cause.
     * 
     * @param message the detail message
     * @param cause   the cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}