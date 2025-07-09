package dev.amir.notes.notes.domain.exceptions;

/**
 * Custom exception for when a note is not found
 * <p>
 * This exception is thrown when attempting to access a note
 * that doesn't exist in the database.
 */
public class NoteNotFoundException extends RuntimeException {

    /**
     * Constructs a NoteNotFoundException with the specified note ID.
     *
     * @param noteId the ID of the note that was not found
     */
    public NoteNotFoundException(String noteId) {
        super("Note not found with ID: " + noteId);
    }
}
