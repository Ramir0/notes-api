package dev.amir.notes.notes.domain.exceptions;

/**
 * Custom exception for validation errors
 * <p>
 * This exception is thrown when validation fails
 * during note processing.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a ValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }
}
