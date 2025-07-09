package dev.amir.notes.notes.application.services;

import dev.amir.notes.notes.application.events.NoteResponseEvent;
import dev.amir.notes.notes.application.mappers.NoteMapper;
import dev.amir.notes.notes.application.requests.NoteRequest;
import dev.amir.notes.notes.application.responses.NoteResponse;
import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.domain.events.EventType;
import dev.amir.notes.notes.domain.exceptions.NoteNotFoundException;
import dev.amir.notes.notes.domain.repositories.NoteRepository;
import dev.amir.notes.notes.infrastructure.data.events.NoteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for NoteServiceImpl.
 * This class tests the functionality of the NoteServiceImpl class, including creating, retrieving,
 * updating, and deleting notes, as well as handling events and errors.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NoteServiceImpl Tests")
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private NoteRequest noteRequest;
    private Note note;
    private NoteResponse noteResponse;
    private NoteResponseEvent noteResponseEvent;
    private NoteEvent noteEvent;

    @BeforeEach
    void setUp() {
        noteRequest = NoteRequest.builder()
                .title("Test Title")
                .content("Test Content")
                .category("Test Category")
                .important(true)
                .tags("tag1, tag2")
                .build();

        note = Note.builder()
                .id("test-id")
                .title("Test Title")
                .content("Test Content")
                .category("Test Category")
                .important(true)
                .tags("tag1, tag2")
                .build();

        noteResponse = NoteResponse.builder()
                .id("test-id")
                .title("Test Title")
                .content("Test Content")
                .category("Test Category")
                .important(true)
                .tags("tag1, tag2")
                .build();

        noteResponseEvent = new NoteResponseEvent(EventType.INSERT, null, "test-id");
        noteEvent = new NoteEvent(EventType.INITIAL, "test-id");
    }

    @Nested
    @DisplayName("Create Note Tests")
    class CreateNoteTests {

        @Test
        @DisplayName("Should create note successfully")
        void shouldCreateNoteSuccessfully() {
            // Given
            when(noteRepository.save(any(Note.class))).thenReturn(Mono.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.createNote(noteRequest))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).save(any(Note.class));
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }

        @Test
        @DisplayName("Should handle error when creating note fails")
        void shouldHandleErrorWhenCreatingNoteFails() {
            // Given
            RuntimeException exception = new RuntimeException("Database error");
            when(noteRepository.save(any(Note.class))).thenReturn(Mono.error(exception));

            // When & Then
            StepVerifier.create(noteService.createNote(noteRequest))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(noteRepository).save(any(Note.class));
        }
    }

    @Nested
    @DisplayName("Get All Notes With Updates Tests")
    class GetAllNotesWithUpdatesTests {

        @Test
        @DisplayName("Should get all notes with updates successfully")
        void shouldGetAllNotesWithUpdatesSuccessfully() {
            // Given
            when(noteRepository.getAllNotesWithUpdates()).thenReturn(Flux.just(noteEvent));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponseEvent(noteEvent))
                        .thenReturn(noteResponseEvent);

                // When & Then
                StepVerifier.create(noteService.getAllNotesWithUpdates())
                        .expectNext(noteResponseEvent)
                        .verifyComplete();

                verify(noteRepository).getAllNotesWithUpdates();
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponseEvent(noteEvent));
            }
        }

        @Test
        @DisplayName("Should handle error in notes updates stream")
        void shouldHandleErrorInNotesUpdatesStream() {
            // Given
            RuntimeException exception = new RuntimeException("Stream error");
            when(noteRepository.getAllNotesWithUpdates()).thenReturn(Flux.error(exception));

            // When & Then
            StepVerifier.create(noteService.getAllNotesWithUpdates())
                    .expectError(RuntimeException.class)
                    .verify();

            verify(noteRepository).getAllNotesWithUpdates();
        }
    }

    @Nested
    @DisplayName("Get All Notes Tests")
    class GetAllNotesTests {

        @Test
        @DisplayName("Should get all notes successfully")
        void shouldGetAllNotesSuccessfully() {
            // Given
            when(noteRepository.getAllNotes()).thenReturn(Flux.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.getAllNotes())
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).getAllNotes();
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }

        @Test
        @DisplayName("Should handle error when fetching all notes fails")
        void shouldHandleErrorWhenFetchingAllNotesFails() {
            // Given
            RuntimeException exception = new RuntimeException("Database error");
            when(noteRepository.getAllNotes()).thenReturn(Flux.error(exception));

            // When & Then
            StepVerifier.create(noteService.getAllNotes())
                    .expectError(RuntimeException.class)
                    .verify();

            verify(noteRepository).getAllNotes();
        }
    }

    @Nested
    @DisplayName("Get Note By ID Tests")
    class GetNoteByIdTests {

        @Test
        @DisplayName("Should get note by ID successfully")
        void shouldGetNoteByIdSuccessfully() {
            // Given
            String noteId = "test-id";
            when(noteRepository.findById(noteId)).thenReturn(Mono.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.getNoteById(noteId))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).findById(noteId);
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }

        @Test
        @DisplayName("Should throw NoteNotFoundException when note not found")
        void shouldThrowNoteNotFoundExceptionWhenNoteNotFound() {
            // Given
            String noteId = "non-existent-id";
            when(noteRepository.findById(noteId)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(noteService.getNoteById(noteId))
                    .expectError(NoteNotFoundException.class)
                    .verify();

            verify(noteRepository).findById(noteId);
        }
    }

    @Nested
    @DisplayName("Update Note Tests")
    class UpdateNoteTests {

        @Test
        @DisplayName("Should update note successfully")
        void shouldUpdateNoteSuccessfully() {
            // Given
            String noteId = "test-id";
            when(noteRepository.findById(noteId)).thenReturn(Mono.just(note));
            when(noteRepository.save(any(Note.class))).thenReturn(Mono.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.updateNote(noteId, noteRequest))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).findById(noteId);
                verify(noteRepository).save(any(Note.class));
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }

        @Test
        @DisplayName("Should throw NoteNotFoundException when updating non-existent note")
        void shouldThrowNoteNotFoundExceptionWhenUpdatingNonExistentNote() {
            // Given
            String noteId = "non-existent-id";
            when(noteRepository.findById(noteId)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(noteService.updateNote(noteId, noteRequest))
                    .expectError(NoteNotFoundException.class)
                    .verify();

            verify(noteRepository).findById(noteId);
            verify(noteRepository, never()).save(any(Note.class));
        }
    }

    @Nested
    @DisplayName("Delete Note Tests")
    class DeleteNoteTests {

        @Test
        @DisplayName("Should delete note successfully")
        void shouldDeleteNoteSuccessfully() {
            // Given
            String noteId = "test-id";
            when(noteRepository.findById(noteId)).thenReturn(Mono.just(note));
            when(noteRepository.delete(note)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(noteService.deleteNote(noteId))
                    .verifyComplete();

            verify(noteRepository).findById(noteId);
            verify(noteRepository).delete(note);
        }

        @Test
        @DisplayName("Should throw NoteNotFoundException when deleting non-existent note")
        void shouldThrowNoteNotFoundExceptionWhenDeletingNonExistentNote() {
            // Given
            String noteId = "non-existent-id";
            when(noteRepository.findById(noteId)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(noteService.deleteNote(noteId))
                    .expectError(NoteNotFoundException.class)
                    .verify();

            verify(noteRepository).findById(noteId);
            verify(noteRepository, never()).delete(any(Note.class));
        }
    }

    @Nested
    @DisplayName("Get Notes By Category Tests")
    class GetNotesByCategoryTests {

        @Test
        @DisplayName("Should get notes by category successfully")
        void shouldGetNotesByCategorySuccessfully() {
            // Given
            String category = "Test Category";
            when(noteRepository.findByCategory(category)).thenReturn(Flux.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.getNotesByCategory(category))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).findByCategory(category);
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }
    }

    @Nested
    @DisplayName("Get Important Notes Tests")
    class GetImportantNotesTests {

        @Test
        @DisplayName("Should get important notes successfully")
        void shouldGetImportantNotesSuccessfully() {
            // Given
            Boolean important = true;
            when(noteRepository.findByImportant(important)).thenReturn(Flux.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.getImportantNotes(important))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).findByImportant(important);
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }
    }

    @Nested
    @DisplayName("Search Notes By Title Tests")
    class SearchNotesByTitleTests {

        @Test
        @DisplayName("Should search notes by title successfully")
        void shouldSearchNotesByTitleSuccessfully() {
            // Given
            String title = "Test";
            when(noteRepository.findByTitleContainingIgnoreCase(title)).thenReturn(Flux.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.searchNotesByTitle(title))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).findByTitleContainingIgnoreCase(title);
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }
    }

    @Nested
    @DisplayName("Search Notes By Content Tests")
    class SearchNotesByContentTests {

        @Test
        @DisplayName("Should search notes by content successfully")
        void shouldSearchNotesByContentSuccessfully() {
            // Given
            String content = "Test";
            when(noteRepository.findByContentContainingIgnoreCase(content)).thenReturn(Flux.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.searchNotesByContent(content))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).findByContentContainingIgnoreCase(content);
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }
    }

    @Nested
    @DisplayName("Get Notes By Tag Tests")
    class GetNotesByTagTests {

        @Test
        @DisplayName("Should get notes by tag successfully")
        void shouldGetNotesByTagSuccessfully() {
            // Given
            String tag = "tag1";
            when(noteRepository.findByTagsContaining(tag)).thenReturn(Flux.just(note));

            try (MockedStatic<NoteMapper> noteMapperMock = mockStatic(NoteMapper.class)) {
                noteMapperMock.when(() -> NoteMapper.mapToNoteResponse(note))
                        .thenReturn(noteResponse);

                // When & Then
                StepVerifier.create(noteService.getNotesByTag(tag))
                        .expectNext(noteResponse)
                        .verifyComplete();

                verify(noteRepository).findByTagsContaining(tag);
                noteMapperMock.verify(() -> NoteMapper.mapToNoteResponse(note));
            }
        }
    }

    @Nested
    @DisplayName("Count Notes By Category Tests")
    class CountNotesByCategoryTests {

        @Test
        @DisplayName("Should count notes by category successfully")
        void shouldCountNotesByCategorySuccessfully() {
            // Given
            String category = "Test Category";
            Long expectedCount = 5L;
            when(noteRepository.countByCategory(category)).thenReturn(Mono.just(expectedCount));

            // When & Then
            StepVerifier.create(noteService.countNotesByCategory(category))
                    .expectNext(expectedCount)
                    .verifyComplete();

            verify(noteRepository).countByCategory(category);
        }

        @Test
        @DisplayName("Should handle error when counting notes by category fails")
        void shouldHandleErrorWhenCountingNotesByCategoryFails() {
            // Given
            String category = "Test Category";
            RuntimeException exception = new RuntimeException("Database error");
            when(noteRepository.countByCategory(category)).thenReturn(Mono.error(exception));

            // When & Then
            StepVerifier.create(noteService.countNotesByCategory(category))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(noteRepository).countByCategory(category);
        }
    }
}
