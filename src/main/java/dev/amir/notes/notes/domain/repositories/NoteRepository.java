package dev.amir.notes.notes.domain.repositories;

import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.infrastructure.data.events.NoteEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This interface defines the contract for a repository that handles
 * operations related to notes in the application.
 * It can be extended to include methods for CRUD operations.
 */
public interface NoteRepository {

    /**
     * Save a note to the repository
     *
     * @param note The note to save
     * @return Mono containing the saved note
     */
    Mono<Note> save(Note note);

    /**
     * Get all notes
     *
     * @return Flux of all notes
     */
    Flux<Note> getAllNotes();

    /**
     * Get all notes with real-time updates
     *
     * @return Flux of notes that emits existing notes and updates
     */
    Flux<NoteEvent> getAllNotesWithUpdates();

    /**
     * Find a note by its ID
     *
     * @param id The ID of the note
     * @return Mono containing the found note or empty if not found
     */
    Mono<Note> findById(String id);

    /**
     * Find notes by category
     *
     * @param category The category to search for
     * @return Flux of notes in the specified category
     */
    Flux<Note> findByCategory(String category);

    /**
     * Find notes marked as important
     *
     * @param important Boolean indicating importance
     * @return Flux of important notes
     */
    Flux<Note> findByImportant(Boolean important);

    /**
     * Find notes by title containing a specific string (case-insensitive)
     *
     * @param title The title text to search for
     * @return Flux of notes with matching titles
     */
    Flux<Note> findByTitleContainingIgnoreCase(String title);

    /**
     * Find notes by content containing a specific string (case-insensitive)
     *
     * @param content The satisfied text to search for
     * @return Flux of notes with matching content
     */
    Flux<Note> findByContentContainingIgnoreCase(String content);

    /**
     * Custom query to find notes by tags
     *
     * @param tag The tag to search for
     * @return Flux of notes containing the specified tag
     */
    Flux<Note> findByTagsContaining(String tag);

    /**
     * Count notes by category
     *
     * @param category The category to count
     * @return Mono containing the count
     */
    Mono<Long> countByCategory(String category);

    /**
     * Delete a note
     *
     * @param note The note to delete
     * @return Mono<Void> indicating completion
     */
    Mono<Void> delete(Note note);
}
