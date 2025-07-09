package dev.amir.notes.notes.presentation.exceptions;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a response for errors in the API.
 * <p>
 * This class is used to encapsulate error details
 * when a request fails due to various reasons.
 */
@Getter
@Builder
public final class ErrorResponse {
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
}
