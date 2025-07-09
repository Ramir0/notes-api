package dev.amir.notes.notes.infrastructure.data.repositories;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import dev.amir.notes.notes.domain.entities.EntityName;
import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.domain.events.EventType;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveChangeStreamOperation;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for NoteRepositoryImpl class.
 * This class tests the CRUD operations and query methods of the NoteRepositoryImpl,
 * ensuring that the repository interacts correctly with the MongoDB database.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NoteRepositoryImpl Tests")
class NoteRepositoryImplTest {

    @Mock
    private NoteMongoRepository noteMongoRepository;

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    private ReactiveChangeStreamOperation.ReactiveChangeStream<Note> reactiveChangeStream;

    @Mock
    private ReactiveChangeStreamOperation.ChangeStreamWithFilterAndProjection<Note> changeStreamWithFilterAndProjection;


    @InjectMocks
    private NoteRepositoryImpl noteRepository;

    private Note note;
    private final String noteId = "507f1f77bcf86cd799439011";

    @BeforeEach
    void setUp() {
        note = Note.builder()
                .id(noteId)
                .title("Test Note")
                .content("Test Content")
                .build();
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save note successfully")
        void shouldSaveNoteSuccessfully() {
            // Given
            when(noteMongoRepository.save(note)).thenReturn(Mono.just(note));

            // When & Then
            StepVerifier.create(noteRepository.save(note))
                    .expectNext(note)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should find note by ID")
        void shouldFindNoteById() {
            // Given
            when(noteMongoRepository.findById(noteId)).thenReturn(Mono.just(note));

            // When & Then
            StepVerifier.create(noteRepository.findById(noteId))
                    .expectNext(note)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should delete note successfully")
        void shouldDeleteNoteSuccessfully() {
            // Given
            when(noteMongoRepository.delete(note)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(noteRepository.delete(note))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Query Methods")
    class QueryMethods {

        @Test
        @DisplayName("Should get all notes")
        void shouldGetAllNotes() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.just(note));

            // When & Then
            StepVerifier.create(noteRepository.getAllNotes())
                    .expectNext(note)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get notes by category")
        void shouldGetNotesByCategory() {
            // Given
            String category = "work";
            when(noteMongoRepository.findByCategory(category)).thenReturn(Flux.just(note));

            // When & Then
            StepVerifier.create(noteRepository.findByCategory(category))
                    .expectNext(note)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should count notes by category")
        void shouldCountNotesByCategory() {
            // Given
            String category = "work";
            when(noteMongoRepository.countByCategory(category)).thenReturn(Mono.just(5L));

            // When & Then
            StepVerifier.create(noteRepository.countByCategory(category))
                    .expectNext(5L)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("getAllNotesWithUpdates() Method")
    class GetAllNotesWithUpdatesMethod {

        @SuppressWarnings("unchecked")
        @BeforeEach
        void setUp() {
            // Setup change stream mock chain
            when(mongoTemplate.changeStream(eq(Note.class)))
                    .thenReturn(reactiveChangeStream);
            when(reactiveChangeStream.withOptions(any(Consumer.class))).thenReturn(reactiveChangeStream);
            when(reactiveChangeStream.watchCollection(EntityName.NOTES))
                    .thenReturn(changeStreamWithFilterAndProjection);
            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.empty());
        }

        @Test
        @DisplayName("Should include existing notes as INITIAL events")
        void shouldIncludeExistingNotesAsInitialEvents() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.just(note));

            // When & Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextMatches(event ->
                            event.getEventType() == EventType.INITIAL &&
                                    event.getBody().equals(note))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should map INSERT change event to NoteEvent")
        void shouldMapInsertChangeEvent() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.empty());

            // When
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> insertEvent = mock(ChangeStreamEvent.class);
            when(insertEvent.getBody()).thenReturn(note);

            @SuppressWarnings("unchecked")
            ChangeStreamDocument<Document> rawInsert = mock(ChangeStreamDocument.class);
            when(rawInsert.getOperationType()).thenReturn(OperationType.INSERT);
            when(insertEvent.getRaw()).thenReturn(rawInsert);

            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.just(insertEvent));

            // Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextMatches(event ->
                            event.getEventType() == EventType.INSERT &&
                                    event.getBody().equals(note))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should map UPDATE change event to NoteEvent")
        void shouldMapUpdateChangeEvent() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.empty());

            // When
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> updateEvent = mock(ChangeStreamEvent.class);
            when(updateEvent.getBody()).thenReturn(note);

            @SuppressWarnings("unchecked")
            ChangeStreamDocument<Document> rawUpdate = mock(ChangeStreamDocument.class);
            when(rawUpdate.getOperationType()).thenReturn(OperationType.UPDATE);
            when(updateEvent.getRaw()).thenReturn(rawUpdate);

            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.just(updateEvent));

            // Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextMatches(event ->
                            event.getEventType() == EventType.UPDATE &&
                                    event.getBody().equals(note))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should map DELETE change event to NoteEvent")
        void shouldMapDeleteChangeEvent() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.empty());

            // When
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> deleteEvent = mock(ChangeStreamEvent.class);

            @SuppressWarnings("unchecked")
            ChangeStreamDocument<Document> rawDelete = mock(ChangeStreamDocument.class);
            when(rawDelete.getOperationType()).thenReturn(OperationType.DELETE);
            when(rawDelete.getDocumentKey()).thenReturn(new BsonDocument("_id", new BsonObjectId()));
            when(deleteEvent.getRaw()).thenReturn(rawDelete);

            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.just(deleteEvent));

            // Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextMatches(event ->
                            event.getEventType() == EventType.DELETE &&
                                    event.getEntityId() != null)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should filter out unknown operation types")
        void shouldFilterOutUnknownOperationTypes() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.empty());

            // When
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> unknownEvent = mock(ChangeStreamEvent.class);

            @SuppressWarnings("unchecked")
            ChangeStreamDocument<Document> rawUnknown = mock(ChangeStreamDocument.class);
            when(rawUnknown.getOperationType()).thenReturn(OperationType.INVALIDATE);
            when(unknownEvent.getRaw()).thenReturn(rawUnknown);

            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.just(unknownEvent));

            // Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextCount(0)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle null raw document in change event")
        void shouldHandleNullRawDocument() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.empty());

            // When
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> nullRawEvent = mock(ChangeStreamEvent.class);
            when(nullRawEvent.getRaw()).thenReturn(null);

            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.just(nullRawEvent));

            // Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextCount(0)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle DELETE event without document key")
        void shouldHandleDeleteEventWithoutDocumentKey() {
            // Given
            when(noteMongoRepository.findAll()).thenReturn(Flux.empty());

            // When
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> deleteEvent = mock(ChangeStreamEvent.class);

            @SuppressWarnings("unchecked")
            ChangeStreamDocument<Document> rawDelete = mock(ChangeStreamDocument.class);
            when(rawDelete.getOperationType()).thenReturn(OperationType.DELETE);
            when(rawDelete.getDocumentKey()).thenReturn(null); // No document key
            when(deleteEvent.getRaw()).thenReturn(rawDelete);

            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.just(deleteEvent));

            // Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextCount(0)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should combine initial notes and change events")
        void shouldCombineInitialNotesAndChangeEvents() {
            // Given existing notes
            Note note1 = Note.builder().id("1").build();
            Note note2 = Note.builder().id("2").build();
            when(noteMongoRepository.findAll()).thenReturn(Flux.just(note1, note2));

            // When
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> insertEvent = mock(ChangeStreamEvent.class);
            @SuppressWarnings("unchecked")
            ChangeStreamEvent<Note> updateEvent = mock(ChangeStreamEvent.class);

            @SuppressWarnings("unchecked")
            ChangeStreamDocument<Document> rawInsert = mock(ChangeStreamDocument.class);
            when(rawInsert.getOperationType()).thenReturn(OperationType.INSERT);
            when(insertEvent.getRaw()).thenReturn(rawInsert);
            when(insertEvent.getBody()).thenReturn(Note.builder().id("3").build());

            @SuppressWarnings("unchecked")
            ChangeStreamDocument<Document> rawUpdate = mock(ChangeStreamDocument.class);
            when(rawUpdate.getOperationType()).thenReturn(OperationType.UPDATE);
            when(updateEvent.getRaw()).thenReturn(rawUpdate);
            when(updateEvent.getBody()).thenReturn(Note.builder().id("4").build());

            when(changeStreamWithFilterAndProjection.listen()).thenReturn(Flux.just(insertEvent, updateEvent));

            // Then
            StepVerifier.create(noteRepository.getAllNotesWithUpdates())
                    .expectNextMatches(event -> event.getEventType() == EventType.INITIAL && "1".equals(event.getBody().getId()))
                    .expectNextMatches(event -> event.getEventType() == EventType.INITIAL && "2".equals(event.getBody().getId()))
                    .expectNextMatches(event -> event.getEventType() == EventType.INSERT && "3".equals(event.getBody().getId()))
                    .expectNextMatches(event -> event.getEventType() == EventType.UPDATE && "4".equals(event.getBody().getId()))
                    .verifyComplete();
        }
    }
}
