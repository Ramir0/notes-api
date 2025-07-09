package dev.amir.notes.notes.application.services;

import dev.amir.notes.notes.application.events.NoteResponseEvent;
import dev.amir.notes.notes.application.mappers.NoteMapper;
import dev.amir.notes.notes.application.requests.NoteRequest;
import dev.amir.notes.notes.application.responses.NoteResponse;
import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.domain.exceptions.NoteNotFoundException;
import dev.amir.notes.notes.domain.repositories.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of NoteService using reactive programming
 * <p>
 * This service provides business logic for note operations
 * and demonstrates reactive programming patterns with error handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;

    @Override
    public Mono<NoteResponse> createNote(NoteRequest noteRequest) {
        log.info("Creating new note with title: {}", noteRequest.getTitle());

        Note note = Note.builder()
                .title(noteRequest.getTitle())
                .content(noteRequest.getContent())
                .category(noteRequest.getCategory())
                .important(noteRequest.getImportant())
                .tags(noteRequest.getTags())
                .build();

        return noteRepository.save(note)
                .map(NoteMapper::mapToNoteResponse)
                .doOnSuccess(savedNote -> log.info("Successfully created note with ID: {}", savedNote.getId()))
                .doOnError(error -> log.error("Error creating note: {}", error.getMessage()));
    }

    @Override
    public Flux<NoteResponseEvent> getAllNotesWithUpdates() {
        return noteRepository.getAllNotesWithUpdates()
                .map(NoteMapper::mapToNoteResponseEvent)
                .doOnSubscribe(subscription -> log.info("Subscribed to note updates"))
                .doOnNext(event -> log.info("Received note update: {}", event.getEntityId()))
                .doOnError(error -> log.error("Error in note updates: {}", error.getMessage()))
                .doOnComplete(() -> log.info("Note updates stream completed"));
    }

    @Override
    public Flux<NoteResponse> getAllNotes() {
        log.info("Fetching all notes");

        return noteRepository.getAllNotes()
                .map(NoteMapper::mapToNoteResponse)
                .doOnComplete(() -> log.info("Successfully fetched all notes"))
                .doOnError(error -> log.error("Error fetching notes: {}", error.getMessage()));
    }

    @Override
    public Mono<NoteResponse> getNoteById(String id) {
        log.info("Fetching note with ID: {}", id);

        return noteRepository.findById(id)
                .map(NoteMapper::mapToNoteResponse)
                .switchIfEmpty(Mono.error(new NoteNotFoundException(id)))
                .doOnSuccess(note -> log.info("Successfully fetched note with ID: {}", id))
                .doOnError(error -> log.error("Error fetching note with ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<NoteResponse> updateNote(String id, NoteRequest noteRequest) {
        log.info("Updating note with ID: {}", id);

        return noteRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoteNotFoundException(id)))
                .map(existingNote -> {
                    existingNote.setTitle(noteRequest.getTitle());
                    existingNote.setContent(noteRequest.getContent());
                    existingNote.setCategory(noteRequest.getCategory());
                    existingNote.setImportant(noteRequest.getImportant());
                    existingNote.setTags(noteRequest.getTags());
                    return existingNote;
                })
                .flatMap(noteRepository::save)
                .map(NoteMapper::mapToNoteResponse)
                .doOnSuccess(updatedNote -> log.info("Successfully updated note with ID: {}", id))
                .doOnError(error -> log.error("Error updating note with ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteNote(String id) {
        log.info("Deleting note with ID: {}", id);

        return noteRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoteNotFoundException(id)))
                .flatMap(noteRepository::delete)
                .doOnSuccess(unused -> log.info("Successfully deleted note with ID: {}", id))
                .doOnError(error -> log.error("Error deleting note with ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Flux<NoteResponse> getNotesByCategory(String category) {
        log.info("Fetching notes by category: {}", category);

        return noteRepository.findByCategory(category)
                .map(NoteMapper::mapToNoteResponse)
                .doOnComplete(() -> log.info("Successfully fetched notes for category: {}", category))
                .doOnError(error -> log.error("Error fetching notes by category {}: {}", category, error.getMessage()));
    }

    @Override
    public Flux<NoteResponse> getImportantNotes(Boolean important) {
        log.info("Fetching important notes: {}", important);

        return noteRepository.findByImportant(important)
                .map(NoteMapper::mapToNoteResponse)
                .doOnComplete(() -> log.info("Successfully fetched important notes"))
                .doOnError(error -> log.error("Error fetching important notes: {}", error.getMessage()));
    }

    @Override
    public Flux<NoteResponse> searchNotesByTitle(String title) {
        log.info("Searching notes by title: {}", title);

        return noteRepository.findByTitleContainingIgnoreCase(title)
                .map(NoteMapper::mapToNoteResponse)
                .doOnComplete(() -> log.info("Successfully searched notes by title: {}", title))
                .doOnError(error -> log.error("Error searching notes by title {}: {}", title, error.getMessage()));
    }

    @Override
    public Flux<NoteResponse> searchNotesByContent(String content) {
        log.info("Searching notes by content: {}", content);

        return noteRepository.findByContentContainingIgnoreCase(content)
                .map(NoteMapper::mapToNoteResponse)
                .doOnComplete(() -> log.info("Successfully searched notes by content"))
                .doOnError(error -> log.error("Error searching notes by content: {}", error.getMessage()));
    }

    @Override
    public Flux<NoteResponse> getNotesByTag(String tag) {
        log.info("Fetching notes by tag: {}", tag);

        return noteRepository.findByTagsContaining(tag)
                .map(NoteMapper::mapToNoteResponse)
                .doOnComplete(() -> log.info("Successfully fetched notes by tag: {}", tag))
                .doOnError(error -> log.error("Error fetching notes by tag {}: {}", tag, error.getMessage()));
    }

    @Override
    public Mono<Long> countNotesByCategory(String category) {
        log.info("Counting notes by category: {}", category);

        return noteRepository.countByCategory(category)
                .doOnSuccess(count -> log.info("Found {} notes in category: {}", count, category))
                .doOnError(error -> log.error("Error counting notes by category {}: {}", category, error.getMessage()));
    }
}
