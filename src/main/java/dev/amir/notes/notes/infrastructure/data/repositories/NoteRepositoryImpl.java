package dev.amir.notes.notes.infrastructure.data.repositories;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import dev.amir.notes.notes.domain.entities.EntityName;
import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.domain.events.EventType;
import dev.amir.notes.notes.domain.repositories.NoteRepository;
import dev.amir.notes.notes.infrastructure.data.events.NoteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Implementation of NoteRepository using reactive programming
 * <p>
 * This repository provides to provide reactive CRUD
 * operations and custom query methods.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class NoteRepositoryImpl implements NoteRepository {
    private final NoteMongoRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Note> save(Note note) {
        return repository.save(note);
    }

    @Override
    public Flux<Note> getAllNotes() {
        return repository.findAll();
    }

    @Override
    public Flux<NoteEvent> getAllNotesWithUpdates() {
        log.info("Streaming notes with real-time updates");

        // Get existing notes first - wrap them as INSERT events
        Flux<NoteEvent> existingNotes = repository.findAll()
                .map(note -> new NoteEvent(EventType.INITIAL, note));

        // Then stream changes
        Flux<NoteEvent> changeStream = mongoTemplate
                .changeStream(Note.class)
                .withOptions(ChangeStreamOptions.ChangeStreamOptionsBuilder::returnFullDocumentOnUpdate)
                .watchCollection(EntityName.NOTES)
                .listen()
                .mapNotNull(event -> {
                    if (event.getRaw() == null || event.getRaw().getOperationType() == null) {
                        return null;
                    }

                    ChangeStreamDocument<Document> raw = event.getRaw();
                    return switch (raw.getOperationType()) {
                        case INSERT -> new NoteEvent(EventType.INSERT, event.getBody());
                        case UPDATE, REPLACE -> new NoteEvent(EventType.UPDATE, event.getBody());
                        case DELETE -> {
                            if (raw.getDocumentKey() == null) {
                                yield null;
                            }
                            String deletedId = raw
                                    .getDocumentKey()
                                    .get("_id")
                                    .asObjectId()
                                    .getValue()
                                    .toString();
                            yield new NoteEvent(EventType.DELETE, deletedId);
                        }
                        default -> null;
                    };
                })
                .filter(Objects::nonNull);

        return Flux.concat(existingNotes, changeStream);
    }

    @Override
    public Mono<Note> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Note> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    public Flux<Note> findByImportant(Boolean important) {
        return repository.findByImportant(important);
    }

    @Override
    public Flux<Note> findByTitleContainingIgnoreCase(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public Flux<Note> findByContentContainingIgnoreCase(String content) {
        return repository.findByContentContainingIgnoreCase(content);
    }

    @Override
    public Flux<Note> findByTagsContaining(String tag) {
        return repository.findByTagsContaining(tag);
    }

    @Override
    public Mono<Long> countByCategory(String category) {
        return repository.countByCategory(category);
    }

    @Override
    public Mono<Void> delete(Note note) {
        return repository.delete(note);
    }
}
