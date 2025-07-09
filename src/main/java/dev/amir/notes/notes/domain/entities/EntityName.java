package dev.amir.notes.notes.domain.entities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class defines the names of entities used in the application.
 * <p>
 * It is used to maintain consistency and avoid hardcoding entity names throughout the codebase.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityName {
    /**
     * The name of the Note entity.
     * This is used as the collection name in MongoDB.
     */
    public static final String NOTES = "notes";
}
