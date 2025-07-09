package dev.amir.notes.notes.application.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for note requests
 * <p>
 * This class represents the structure of note data
 * sent to the API endpoints for creating or updating notes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {

    /**
     * Title of the note.
     * This field is required and must not be blank.
     */
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    /**
     * Content of the note.
     * This field is required and must not be blank.
     */
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;

    /**
     * Category of the note.
     * This field is optional and can be used to organize notes.
     */
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    /**
     * Indicates whether the note is marked as important.
     * This field is optional and defaults to false.
     */
    @Builder.Default
    private Boolean important = false;

    /**
     * Tags associated with the note.
     * This field is optional and can be used to add metadata to the note.
     */
    private String tags;
}
