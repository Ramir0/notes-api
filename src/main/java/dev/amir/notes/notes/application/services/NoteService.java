package dev.amir.notes.notes.application.services;

import dev.amir.notes.notes.application.events.NoteResponseEvent;
import dev.amir.notes.notes.application.requests.NoteRequest;
import dev.amir.notes.notes.application.responses.NoteResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for Note operations
 * <p>
 * This interface defines the contract for note-related business operations
 * using reactive programming paradigms with Mono and Flux.
 */
public interface NoteService {

    /**
     * Create a new note
     *
     * @param noteRequest The note creation request
     * @return Mono containing the created note response
     */
    Mono<NoteResponse> createNote(NoteRequest noteRequest);

    /**
     * Get all notes
     *
     * @return Flux of all note responses
     */
    Flux<NoteResponse> getAllNotes();

    /**
     * Get all notes with updates
     *
     * @return Flux of all notes
     */
    Flux<NoteResponseEvent> getAllNotesWithUpdates();

    /**
     * Get a note by ID
     *
     * @param id The note ID
     * @return Mono containing the note response
     */
    Mono<NoteResponse> getNoteById(String id);

    /**
     * Update an existing note
     *
     * @param id          The note ID
     * @param noteRequest The note update request
     * @return Mono containing the updated note response
     */
    Mono<NoteResponse> updateNote(String id, NoteRequest noteRequest);

    /**
     * Delete a note by ID
     *
     * @param id The note ID
     * @return Mono<Void> indicating completion
     */
    Mono<Void> deleteNote(String id);

    /**
     * Get notes by category
     *
     * @param category The category to filter by
     * @return Flux of note responses in the specified category
     */
    Flux<NoteResponse> getNotesByCategory(String category);

    /**
     * Get important notes
     *
     * @param important Boolean indicating importance
     * @return Flux of important note responses
     */
    Flux<NoteResponse> getImportantNotes(Boolean important);

    /**
     * Search notes by title
     *
     * @param title The title text to search for
     * @return Flux of note responses with matching titles
     */
    Flux<NoteResponse> searchNotesByTitle(String title);

    /**
     * Search notes by content
     *
     * @param content The satisfied text to search for
     * @return Flux of note responses with matching content
     */
    Flux<NoteResponse> searchNotesByContent(String content);

    /**
     * Get notes by tag
     *
     * @param tag The tag to search for
     * @return Flux of note responses containing the specified tag
     */
    Flux<NoteResponse> getNotesByTag(String tag);

    /**
     * Count notes by category
     *
     * @param category The category to count
     * @return Mono containing the count
     */
    Mono<Long> countNotesByCategory(String category);
}