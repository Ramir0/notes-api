package dev.amir.notes.notes.presentation.exceptions;

import dev.amir.notes.notes.domain.exceptions.NoteNotFoundException;
import dev.amir.notes.notes.domain.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler class.
 * This class tests the exception handling methods for various exceptions
 * that can occur in the application, ensuring that the responses are correctly formatted.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private BindingResult bindingResult;

    private static final String TEST_NOTE_ID = "test-note-id";
    private static final String TEST_MESSAGE = "Note not found with ID: " + TEST_NOTE_ID;
    private static final String TEST_PATH = "/notes";

    @Nested
    @DisplayName("NoteNotFoundException Handling")
    class NoteNotFoundExceptionHandling {

        @Test
        @DisplayName("Should handle NoteNotFoundException with correct response")
        void shouldHandleNoteNotFoundException() {
            // Given
            NoteNotFoundException exception = new NoteNotFoundException(TEST_NOTE_ID);

            // When
            Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleNoteNotFoundException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                        ErrorResponse body = response.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(body.getError()).isEqualTo("Not Found");
                        assertThat(body.getMessage()).isEqualTo(TEST_MESSAGE);
                        assertThat(body.getPath()).isEqualTo(TEST_PATH);
                        assertThat(body.getTimestamp()).isBeforeOrEqualTo(Instant.now());
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("ValidationException Handling")
    class ValidationExceptionHandling {

        @Test
        @DisplayName("Should handle ValidationException with correct response")
        void shouldHandleValidationException() {
            // Given
            ValidationException exception = new ValidationException(TEST_MESSAGE);

            // When
            Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleValidationException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                        ErrorResponse body = response.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(body.getError()).isEqualTo("Validation Error");
                        assertThat(body.getMessage()).isEqualTo(TEST_MESSAGE);
                        assertThat(body.getPath()).isEqualTo(TEST_PATH);
                        assertThat(body.getTimestamp()).isBeforeOrEqualTo(Instant.now());
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("WebExchangeBindException Handling")
    class WebExchangeBindExceptionHandling {

        @Test
        @DisplayName("Should handle WebExchangeBindException with validation errors")
        void shouldHandleWebExchangeBindException() {
            // Given
            WebExchangeBindException exception = mock(WebExchangeBindException.class);
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(
                    new FieldError("object", "title", "Title is required"),
                    new FieldError("object", "content", "Content is too short")
            ));

            // When
            Mono<ResponseEntity<ValidationErrorResponse>> result =
                    exceptionHandler.handleWebExchangeBindException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                        ValidationErrorResponse body = response.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(body.getError()).isEqualTo("Validation Failed");
                        assertThat(body.getMessage()).isEqualTo("Input validation failed");
                        assertThat(body.getPath()).isEqualTo(TEST_PATH);
                        assertThat(body.getTimestamp()).isBeforeOrEqualTo(Instant.now());

                        Map<String, String> errors = body.getValidationErrors();
                        assertThat(errors)
                                .hasSize(2)
                                .containsEntry("title", "Title is required")
                                .containsEntry("content", "Content is too short");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle WebExchangeBindException with empty validation errors")
        void shouldHandleWebExchangeBindExceptionWithEmptyErrors() {
            // Given
            WebExchangeBindException exception = mock(WebExchangeBindException.class);
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

            // When
            Mono<ResponseEntity<ValidationErrorResponse>> result =
                    exceptionHandler.handleWebExchangeBindException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                        ValidationErrorResponse body = response.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getValidationErrors()).isEmpty();
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException Handling")
    class IllegalArgumentExceptionHandling {

        @Test
        @DisplayName("Should handle IllegalArgumentException with correct response")
        void shouldHandleIllegalArgumentException() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException(TEST_MESSAGE);

            // When
            Mono<ResponseEntity<ErrorResponse>> result =
                    exceptionHandler.handleIllegalArgumentException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                        ErrorResponse body = response.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(body.getError()).isEqualTo("Bad Request");
                        assertThat(body.getMessage()).isEqualTo(TEST_MESSAGE);
                        assertThat(body.getPath()).isEqualTo(TEST_PATH);
                        assertThat(body.getTimestamp()).isBeforeOrEqualTo(Instant.now());
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Generic Exception Handling")
    class GenericExceptionHandling {

        @Test
        @DisplayName("Should handle generic Exception with correct response")
        void shouldHandleGenericException() {
            // Given
            Exception exception = new Exception(TEST_MESSAGE);

            // When
            Mono<ResponseEntity<ErrorResponse>> result =
                    exceptionHandler.handleGenericException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                        ErrorResponse body = response.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        assertThat(body.getError()).isEqualTo("Internal Server Error");
                        assertThat(body.getMessage()).isEqualTo("An unexpected error occurred");
                        assertThat(body.getPath()).isEqualTo(TEST_PATH);
                        assertThat(body.getTimestamp()).isBeforeOrEqualTo(Instant.now());
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle RuntimeExceptions correctly")
        void shouldHandleRuntimeExceptions() {
            // Given
            RuntimeException exception = new RuntimeException(TEST_MESSAGE);

            // When
            Mono<ResponseEntity<ErrorResponse>> result =
                    exceptionHandler.handleGenericException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                        assertThat(response.getBody()).isNotNull();
                        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null exception messages")
        void shouldHandleNullExceptionMessages() {
            // Given
            Exception exception = new Exception((String) null);

            // When
            Mono<ResponseEntity<ErrorResponse>> result =
                    exceptionHandler.handleGenericException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                        assertThat(response.getBody()).isNotNull();
                        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle very long exception messages")
        void shouldHandleLongExceptionMessages() {
            // Given
            String longMessage = "a".repeat(1000);
            ValidationException exception = new ValidationException(longMessage);

            // When
            Mono<ResponseEntity<ErrorResponse>> result =
                    exceptionHandler.handleValidationException(exception);

            // Then
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertThat(response.getBody()).isNotNull();
                        assertThat(response.getBody().getMessage()).hasSize(1000);
                    })
                    .verifyComplete();
        }
    }
}
