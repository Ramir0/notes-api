package dev.amir.notes.notes.presentation.controllers.v1;

import dev.amir.notes.notes.application.events.NoteResponseEvent;
import dev.amir.notes.notes.application.requests.NoteRequest;
import dev.amir.notes.notes.application.responses.NoteResponse;
import dev.amir.notes.notes.application.services.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST Controller for Notes API
 * <p>
 * This controller provides reactive endpoints for all note operations
 * using Spring WebFlux and demonstrates proper reactive patterns.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "Reactive Notes API for managing personal notes")
public class NotesController {

    private final NoteService noteService;

    @Operation(summary = "Create a new note", description = "Creates a new note with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<NoteResponse>> createNote(
            @Valid @RequestBody NoteRequest noteRequest) {

        log.info("Received request to create note with title: {}", noteRequest.getTitle());

        return noteService.createNote(noteRequest)
                .map(noteResponse -> ResponseEntity.status(HttpStatus.CREATED).body(noteResponse))
                .doOnSuccess(response -> log.info("Successfully created note"))
                .doOnError(error -> log.error("Error creating note: {}", error.getMessage()));
    }

    @Operation(summary = "Get all notes", description = "Retrieves all notes from the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved notes")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<NoteResponse> getAllNotes() {
        log.info("Received request to get all notes");

        return noteService.getAllNotes()
                .doOnComplete(() -> log.info("Successfully retrieved all notes"))
                .doOnError(error -> log.error("Error retrieving notes: {}", error.getMessage()));
    }

    @Operation(summary = "Get all notes with updates", description = "Retrieves all notes and streams updates in real-time")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved notes with updates")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NoteResponseEvent> getAllNotesStream() {
        log.info("Received request to get all notes with updates");

        return noteService.getAllNotesWithUpdates()
                .doOnSubscribe(subscription -> log.info("Subscribed to note updates"))
                .doOnNext(event -> log.info("Received note update: {}", event.getEntityId()))
                .doOnComplete(() -> log.info("Note updates stream completed"))
                .doOnError(error -> log.error("Error in note updates stream: {}", error.getMessage()));
    }

    @Operation(summary = "Get note by ID", description = "Retrieves a specific note by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note found"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<NoteResponse>> getNoteById(
            @Parameter(description = "Note ID", required = true) @PathVariable String id) {

        log.info("Received request to get note with ID: {}", id);

        return noteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Successfully retrieved note with ID: {}", id))
                .doOnError(error -> log.error("Error retrieving note with ID {}: {}", id,
                        error.getMessage()));
    }

    @Operation(summary = "Update note", description = "Updates an existing note with new information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note updated successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<NoteResponse>> updateNote(
            @Parameter(description = "Note ID", required = true) @PathVariable String id,
            @Valid @RequestBody NoteRequest noteRequest) {

        log.info("Received request to update note with ID: {}", id);

        return noteService.updateNote(id, noteRequest)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Successfully updated note with ID: {}", id))
                .doOnError(error -> log.error("Error updating note with ID {}: {}", id,
                        error.getMessage()));
    }

    @Operation(summary = "Delete note", description = "Deletes a note by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteNote(
            @Parameter(description = "Note ID", required = true) @PathVariable String id) {

        log.info("Received request to delete note with ID: {}", id);

        return noteService.deleteNote(id)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().<Void>build()))
                .doOnSuccess(response -> log.info("Successfully deleted note with ID: {}", id))
                .doOnError(error -> log.error("Error deleting note with ID {}: {}", id,
                        error.getMessage()));
    }

    @Operation(summary = "Get notes by category", description = "Retrieves all notes in a specific category")
    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<NoteResponse> getNotesByCategory(
            @Parameter(description = "Category name", required = true) @PathVariable String category) {

        log.info("Received request to get notes by category: {}", category);

        return noteService.getNotesByCategory(category)
                .doOnComplete(() -> log.info("Successfully retrieved notes for category: {}", category))
                .doOnError(error -> log.error("Error retrieving notes by category {}: {}", category,
                        error.getMessage()));
    }

    @Operation(summary = "Get important notes", description = "Retrieves all notes marked as important")
    @GetMapping(value = "/important", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<NoteResponse> getImportantNotes(
            @Parameter(description = "Important flag (default: true)") @RequestParam(defaultValue = "true") Boolean important) {

        log.info("Received request to get important notes: {}", important);

        return noteService.getImportantNotes(important)
                .doOnComplete(() -> log.info("Successfully retrieved important notes"))
                .doOnError(error -> log.error("Error retrieving important notes: {}",
                        error.getMessage()));
    }

    @Operation(summary = "Search notes by title", description = "Searches notes by title containing the specified text")
    @GetMapping(value = "/search/title", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<NoteResponse> searchNotesByTitle(
            @Parameter(description = "Title search text", required = true) @RequestParam String title) {

        log.info("Received request to search notes by title: {}", title);

        return noteService.searchNotesByTitle(title)
                .doOnComplete(() -> log.info("Successfully searched notes by title: {}", title))
                .doOnError(error -> log.error("Error searching notes by title {}: {}", title,
                        error.getMessage()));
    }

    @Operation(summary = "Search notes by content", description = "Searches notes by content containing the specified text")
    @GetMapping(value = "/search/content", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<NoteResponse> searchNotesByContent(
            @Parameter(description = "Content search text", required = true) @RequestParam String content) {

        log.info("Received request to search notes by content");

        return noteService.searchNotesByContent(content)
                .doOnComplete(() -> log.info("Successfully searched notes by content"))
                .doOnError(error -> log.error("Error searching notes by content: {}",
                        error.getMessage()));
    }

    @Operation(summary = "Get notes by tag", description = "Retrieves all notes containing the specified tag")
    @GetMapping(value = "/tag/{tag}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<NoteResponse> getNotesByTag(
            @Parameter(description = "Tag name", required = true) @PathVariable String tag) {

        log.info("Received request to get notes by tag: {}", tag);

        return noteService.getNotesByTag(tag)
                .doOnComplete(() -> log.info("Successfully retrieved notes by tag: {}", tag))
                .doOnError(error -> log.error("Error retrieving notes by tag {}: {}", tag,
                        error.getMessage()));
    }

    @Operation(summary = "Count notes by category", description = "Returns the count of notes in a specific category")
    @GetMapping(value = "/count/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Long>> countNotesByCategory(
            @Parameter(description = "Category name", required = true) @PathVariable String category) {

        log.info("Received request to count notes by category: {}", category);

        return noteService.countNotesByCategory(category)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Successfully counted notes for category: {}",
                        category))
                .doOnError(error -> log.error("Error counting notes by category {}: {}", category,
                        error.getMessage()));
    }
}
