package dev.amir.notes.notes.application.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Note entity.
 * <p>
 * This class represents the structure of note data returned by the API endpoints.
 * It includes fields for note details and uses Lombok annotations to reduce boilerplate code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {

    /**
     * Unique identifier for the note.
     * This field is used as the primary key in the MongoDB collection.
     */
    private String id;

    /**
     * Title of the note.
     * This field is required and must not be blank.
     */
    private String title;

    /**
     * Content of the note.
     * This field is required and must not be blank.
     */
    private String content;

    /**
     * Category of the note.
     * This field is optional and can be used to organize notes.
     */
    private String category;

    /**
     * Indicates whether the note is marked as important.
     * This field is optional and defaults to false.
     */
    private Boolean important;

    /**
     * Timestamp when the note was created.
     * This field is automatically populated by Spring Data.
     */
    private Instant createdAt;

    /**
     * Timestamp when the note was last updated.
     * This field is automatically populated by Spring Data.
     */
    private Instant updatedAt;

    /**
     * Tags associated with the note.
     * This field is optional and can be used to add metadata to the note.
     */
    private String tags;
}
