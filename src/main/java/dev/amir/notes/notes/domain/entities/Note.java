package dev.amir.notes.notes.domain.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Note entity representing a note document in MongoDB
 * <p>
 * This class uses Lombok annotations to reduce boilerplate code
 * and Spring Data MongoDB annotations for document mapping.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = EntityName.NOTES)
public class Note {

    /**
     * Unique identifier for the note.
     * This field is used as the primary key in the MongoDB collection.
     */
    @Id
    private String id;

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
     * Timestamp when the note was created.
     * This field is automatically populated by Spring Data.
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Timestamp when the note was last updated.
     * This field is automatically populated by Spring Data.
     */
    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Tags associated with the note.
     * This field is optional and can be used to add metadata to the note.
     */
    private String tags;
}
