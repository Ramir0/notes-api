package dev.amir.notes.notes.presentation.controllers.v1;

import dev.amir.notes.notes.application.events.NoteResponseEvent;
import dev.amir.notes.notes.application.requests.NoteRequest;
import dev.amir.notes.notes.application.responses.NoteResponse;
import dev.amir.notes.notes.application.services.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for NotesController class.
 * This class tests the REST endpoints for managing notes,
 * ensuring that the controller methods interact correctly with the NoteService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotesController Tests")
class NotesControllerTest {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NotesController notesController;

    private WebTestClient webTestClient;
    private NoteRequest noteRequest;
    private NoteResponse noteResponse;
    private NoteResponseEvent noteResponseEvent;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(notesController).build();

        noteRequest = NoteRequest.builder()
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

        noteResponseEvent = new NoteResponseEvent(null, noteResponse, "test-id");
    }

    @Nested
    @DisplayName("POST /api/v1/notes")
    class CreateNoteEndpoint {

        @Test
        @DisplayName("Should create note and return 201 Created")
        void shouldCreateNoteAndReturnCreated() {
            // Given
            when(noteService.createNote(any(NoteRequest.class)))
                    .thenReturn(Mono.just(noteResponse));

            // When & Then
            webTestClient.post().uri("/api/v1/notes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(noteRequest)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(NoteResponse.class)
                    .isEqualTo(noteResponse);
        }

        @Test
        @DisplayName("Should return 400 Bad Request for invalid input")
        void shouldReturnBadRequestForInvalidInput() {
            // Given
            NoteRequest invalidRequest = NoteRequest.builder().build();

            // When & Then
            webTestClient.post().uri("/api/v1/notes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes")
    class GetAllNotesEndpoint {

        @Test
        @DisplayName("Should get all notes and return 200 OK")
        void shouldGetAllNotesAndReturnOk() {
            // Given
            when(noteService.getAllNotes())
                    .thenReturn(Flux.just(noteResponse));

            // When & Then
            webTestClient.get().uri("/api/v1/notes")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(NoteResponse.class)
                    .contains(noteResponse);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/stream")
    class GetAllNotesStreamEndpoint {

        @Test
        @DisplayName("Should stream note updates with text/event-stream")
        void shouldStreamNoteUpdates() {
            // Given
            when(noteService.getAllNotesWithUpdates())
                    .thenReturn(Flux.just(noteResponseEvent));

            // When & Then
            webTestClient.get().uri("/api/v1/notes/stream")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(new MediaType(MediaType.TEXT_EVENT_STREAM, StandardCharsets.UTF_8))
                    .expectBodyList(NoteResponseEvent.class)
                    .hasSize(1)
                    .contains(noteResponseEvent);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/{id}")
    class GetNoteByIdEndpoint {

        @Test
        @DisplayName("Should get note by ID and return 200 OK")
        void shouldGetNoteByIdAndReturnOk() {
            // Given
            String noteId = "test-id";
            when(noteService.getNoteById(noteId))
                    .thenReturn(Mono.just(noteResponse));

            // When & Then
            webTestClient.get().uri("/api/v1/notes/{id}", noteId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(NoteResponse.class)
                    .isEqualTo(noteResponse);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/notes/{id}")
    class UpdateNoteEndpoint {

        @Test
        @DisplayName("Should update note and return 200 OK")
        void shouldUpdateNoteAndReturnOk() {
            // Given
            String noteId = "test-id";
            when(noteService.updateNote(noteId, noteRequest))
                    .thenReturn(Mono.just(noteResponse));

            // When & Then
            webTestClient.put().uri("/api/v1/notes/{id}", noteId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(noteRequest)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(NoteResponse.class)
                    .isEqualTo(noteResponse);
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/notes/{id}")
    class DeleteNoteEndpoint {

        @Test
        @DisplayName("Should delete note and return 204 No Content")
        void shouldDeleteNoteAndReturnNoContent() {
            // Given
            String noteId = "test-id";
            when(noteService.deleteNote(noteId))
                    .thenReturn(Mono.empty());

            // When & Then
            webTestClient.delete().uri("/api/v1/notes/{id}", noteId)
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/category/{category}")
    class GetNotesByCategoryEndpoint {

        @Test
        @DisplayName("Should get notes by category and return 200 OK")
        void shouldGetNotesByCategoryAndReturnOk() {
            // Given
            String category = "Test Category";
            when(noteService.getNotesByCategory(category))
                    .thenReturn(Flux.just(noteResponse));

            // When & Then
            webTestClient.get().uri("/api/v1/notes/category/{category}", category)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(NoteResponse.class)
                    .contains(noteResponse);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/important")
    class GetImportantNotesEndpoint {

        @Test
        @DisplayName("Should get important notes and return 200 OK")
        void shouldGetImportantNotesAndReturnOk() {
            // Given
            when(noteService.getImportantNotes(true))
                    .thenReturn(Flux.just(noteResponse));

            // When & Then
            webTestClient.get().uri("/api/v1/notes/important?important=true")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(NoteResponse.class)
                    .contains(noteResponse);
        }

        @Test
        @DisplayName("Should use default important=true when parameter is missing")
        void shouldUseDefaultImportantValue() {
            // Given
            when(noteService.getImportantNotes(true))
                    .thenReturn(Flux.just(noteResponse));

            // When & Then
            webTestClient.get().uri("/api/v1/notes/important")
                    .exchange()
                    .expectStatus().isOk();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/search/title")
    class SearchNotesByTitleEndpoint {

        @Test
        @DisplayName("Should search notes by title and return 200 OK")
        void shouldSearchNotesByTitleAndReturnOk() {
            // Given
            String title = "Test";
            when(noteService.searchNotesByTitle(title))
                    .thenReturn(Flux.just(noteResponse));

            // When & Then
            webTestClient.get().uri(uriBuilder -> uriBuilder
                            .path("/api/v1/notes/search/title")
                            .queryParam("title", title)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(NoteResponse.class)
                    .contains(noteResponse);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/search/content")
    class SearchNotesByContentEndpoint {

        @Test
        @DisplayName("Should search notes by content and return 200 OK")
        void shouldSearchNotesByContentAndReturnOk() {
            // Given
            String content = "Content";
            when(noteService.searchNotesByContent(content))
                    .thenReturn(Flux.just(noteResponse));

            // When & Then
            webTestClient.get().uri(uriBuilder -> uriBuilder
                            .path("/api/v1/notes/search/content")
                            .queryParam("content", content)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(NoteResponse.class)
                    .contains(noteResponse);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/tag/{tag}")
    class GetNotesByTagEndpoint {

        @Test
        @DisplayName("Should get notes by tag and return 200 OK")
        void shouldGetNotesByTagAndReturnOk() {
            // Given
            String tag = "tag1";
            when(noteService.getNotesByTag(tag))
                    .thenReturn(Flux.just(noteResponse));

            // When & Then
            webTestClient.get().uri("/api/v1/notes/tag/{tag}", tag)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(NoteResponse.class)
                    .contains(noteResponse);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/count/category/{category}")
    class CountNotesByCategoryEndpoint {

        @Test
        @DisplayName("Should count notes by category and return 200 OK")
        void shouldCountNotesByCategoryAndReturnOk() {
            // Given
            String category = "Test Category";
            Long count = 5L;
            when(noteService.countNotesByCategory(category))
                    .thenReturn(Mono.just(count));

            // When & Then
            webTestClient.get().uri("/api/v1/notes/count/category/{category}", category)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Long.class)
                    .isEqualTo(count);
        }
    }
}
