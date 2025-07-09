package dev.amir.notes.notes.presentation.exceptions;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a response for validation errors in the API.
 * <p>
 * This class is used to encapsulate validation error details
 * when a request fails due to validation constraints.
 */
@Getter
@Builder
public final class ValidationErrorResponse {
    /**
     * The timestamp when the error occurred.
     */
    private Instant timestamp;

    /**
     * The HTTP status code of the error.
     */
    private int status;

    /**
     * A short description of the error.
     */
    private String error;

    /**
     * A detailed message about the error.
     */
    private String message;

    /**
     * The path of the request that caused the error.
     */
    private String path;

    /**
     * A map of field-specific validation errors.
     */
    private Map<String, String> validationErrors;
}
