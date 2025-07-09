package dev.amir.notes.notes.application.mappers;

import dev.amir.notes.notes.application.events.NoteResponseEvent;
import dev.amir.notes.notes.application.responses.NoteResponse;
import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.domain.events.EventType;
import dev.amir.notes.notes.infrastructure.data.events.NoteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Unit tests for NoteMapper.
 * This class tests the functionality of the NoteMapper class, including mapping Note entities to NoteResponse DTOs
 * and NoteEvent to NoteResponseEvent.
 */
@DisplayName("NoteMapper Tests")
class NoteMapperTest {

    private Note note;
    private NoteEvent noteEvent;
    private Instant testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2025, 7, 1, 12, 0, 0).toInstant(ZoneOffset.UTC);

        note = Note.builder()
                .id("test-id")
                .title("Test Title")
                .content("Test Content")
                .category("Test Category")
                .important(true)
                .createdAt(testDateTime)
                .updatedAt(testDateTime.plus(30, ChronoUnit.MINUTES))
                .tags("tag1, tag2, tag3")
                .build();
    }

    @Nested
    @DisplayName("Map to NoteResponse Tests")
    class MapToNoteResponseTests {

        @Test
        @DisplayName("Should map Note to NoteResponse successfully with all fields")
        void shouldMapNoteToNoteResponseSuccessfullyWithAllFields() {
            // When
            NoteResponse result = NoteMapper.mapToNoteResponse(note);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponse mapping",
                    () -> assertThat(result.getId()).isEqualTo("test-id"),
                    () -> assertThat(result.getTitle()).isEqualTo("Test Title"),
                    () -> assertThat(result.getContent()).isEqualTo("Test Content"),
                    () -> assertThat(result.getCategory()).isEqualTo("Test Category"),
                    () -> assertThat(result.getImportant()).isTrue(),
                    () -> assertThat(result.getCreatedAt()).isEqualTo(testDateTime),
                    () -> assertThat(result.getUpdatedAt()).isEqualTo(testDateTime.plus(30, ChronoUnit.MINUTES)),
                    () -> assertThat(result.getTags()).isEqualTo("tag1, tag2, tag3")
            );
        }

        @Test
        @DisplayName("Should map Note to NoteResponse with null fields")
        void shouldMapNoteToNoteResponseWithNullFields() {
            // Given
            Note noteWithNulls = Note.builder()
                    .id("test-id")
                    .title(null)
                    .content(null)
                    .category(null)
                    .important(null)
                    .createdAt(null)
                    .updatedAt(null)
                    .tags(null)
                    .build();

            // When
            NoteResponse result = NoteMapper.mapToNoteResponse(noteWithNulls);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponse mapping with nulls",
                    () -> assertThat(result.getId()).isEqualTo("test-id"),
                    () -> assertThat(result.getTitle()).isNull(),
                    () -> assertThat(result.getContent()).isNull(),
                    () -> assertThat(result.getCategory()).isNull(),
                    () -> assertThat(result.getImportant()).isNull(),
                    () -> assertThat(result.getCreatedAt()).isNull(),
                    () -> assertThat(result.getUpdatedAt()).isNull(),
                    () -> assertThat(result.getTags()).isNull()
            );
        }

        @Test
        @DisplayName("Should map Note to NoteResponse with empty tags list")
        void shouldMapNoteToNoteResponseWithEmptyTagsList() {
            // Given
            Note noteWithEmptyTags = Note.builder()
                    .id("test-id")
                    .title("Test Title")
                    .content("Test Content")
                    .category("Test Category")
                    .important(false)
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .tags("")
                    .build();

            // When
            NoteResponse result = NoteMapper.mapToNoteResponse(noteWithEmptyTags);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponse mapping with empty tags",
                    () -> assertThat(result.getId()).isEqualTo("test-id"),
                    () -> assertThat(result.getTitle()).isEqualTo("Test Title"),
                    () -> assertThat(result.getContent()).isEqualTo("Test Content"),
                    () -> assertThat(result.getCategory()).isEqualTo("Test Category"),
                    () -> assertThat(result.getImportant()).isFalse(),
                    () -> assertThat(result.getCreatedAt()).isEqualTo(testDateTime),
                    () -> assertThat(result.getUpdatedAt()).isEqualTo(testDateTime),
                    () -> assertThat(result.getTags()).isEmpty()
            );
        }

        @Test
        @DisplayName("Should map Note to NoteResponse with single tag")
        void shouldMapNoteToNoteResponseWithSingleTag() {
            // Given
            Note noteWithSingleTag = Note.builder()
                    .id("test-id")
                    .title("Test Title")
                    .content("Test Content")
                    .category("Test Category")
                    .important(true)
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .tags("single-tag")
                    .build();

            // When
            NoteResponse result = NoteMapper.mapToNoteResponse(noteWithSingleTag);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponse mapping with single tag",
                    () -> assertThat(result.getTags()).isNotBlank(),
                    () -> assertThat(result.getTags()).isEqualTo("single-tag")
            );
        }

        @Test
        @DisplayName("Should not map Note to NoteResponse with null value")
        void shouldNotMapNoteToNoteResponseWithNullValue() {
            // When & Then
            assertThat(NoteMapper.mapToNoteResponse(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Map to NoteResponseEvent Tests")
    class MapToNoteResponseEventTests {

        @Test
        @DisplayName("Should map NoteEvent to NoteResponseEvent successfully with body")
        void shouldMapNoteEventToNoteResponseEventSuccessfullyWithBody() {
            // Given
            EventType eventType = EventType.INSERT;
            noteEvent = new NoteEvent(eventType, note);

            // When
            NoteResponseEvent result = NoteMapper.mapToNoteResponseEvent(noteEvent);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getBody()).isNotNull();
            assertAll("NoteResponseEvent mapping with body",
                    () -> assertThat(result.getEventType()).isEqualTo(eventType),
                    () -> assertThat(result.getEntityId()).isEqualTo("test-id"),
                    () -> assertThat(result.getBody().getId()).isEqualTo("test-id"),
                    () -> assertThat(result.getBody().getTitle()).isEqualTo("Test Title"),
                    () -> assertThat(result.getBody().getContent()).isEqualTo("Test Content"),
                    () -> assertThat(result.getBody().getCategory()).isEqualTo("Test Category"),
                    () -> assertThat(result.getBody().getImportant()).isTrue(),
                    () -> assertThat(result.getBody().getTags()).isEqualTo("tag1, tag2, tag3")
            );
        }

        @Test
        @DisplayName("Should map NoteEvent to NoteResponseEvent with null body")
        void shouldMapNoteEventToNoteResponseEventWithNullBody() {
            // Given
            EventType eventType = EventType.DELETE;
            String entityId = "test-entity-id";
            noteEvent = new NoteEvent(eventType, entityId);

            // When
            NoteResponseEvent result = NoteMapper.mapToNoteResponseEvent(noteEvent);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponseEvent mapping with null body",
                    () -> assertThat(result.getEventType()).isEqualTo(eventType),
                    () -> assertThat(result.getEntityId()).isEqualTo(entityId),
                    () -> assertThat(result.getBody()).isNull()
            );
        }

        @Test
        @DisplayName("Should map NoteEvent to NoteResponseEvent with different event types")
        void shouldMapNoteEventToNoteResponseEventWithDifferentEventTypes() {
            // Given
            EventType[] eventTypes = {EventType.INSERT, EventType.UPDATE, EventType.DELETE};
            String entityId = "test-entity-id";

            for (EventType eventType : eventTypes) {
                noteEvent = new NoteEvent(eventType, entityId);

                // When
                NoteResponseEvent result = NoteMapper.mapToNoteResponseEvent(noteEvent);

                // Then
                assertThat(result).isNotNull();
                assertAll("NoteResponseEvent mapping for " + eventType,
                        () -> assertThat(result.getEventType()).isEqualTo(eventType),
                        () -> assertThat(result.getEntityId()).isEqualTo(entityId),
                        () -> assertThat(result.getBody()).isNull()
                );
            }
        }

        @Test
        @DisplayName("Should map NoteEvent to NoteResponseEvent with null event type")
        void shouldMapNoteEventToNoteResponseEventWithNullEventType() {
            // Given
            noteEvent = new NoteEvent(null, note);

            // When
            NoteResponseEvent result = NoteMapper.mapToNoteResponseEvent(noteEvent);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponseEvent mapping with null event type",
                    () -> assertThat(result.getEventType()).isNull(),
                    () -> assertThat(result.getEntityId()).isEqualTo("test-id"),
                    () -> assertThat(result.getBody()).isNotNull()
            );
        }

        @Test
        @DisplayName("Should map NoteEvent to NoteResponseEvent with null entity ID")
        void shouldMapNoteEventToNoteResponseEventWithNullEntityId() {
            // Given
            EventType eventType = EventType.INSERT;
            noteEvent = new NoteEvent(eventType, new Note());

            // When
            NoteResponseEvent result = NoteMapper.mapToNoteResponseEvent(noteEvent);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponseEvent mapping with null entity ID",
                    () -> assertThat(result.getEventType()).isEqualTo(eventType),
                    () -> assertThat(result.getEntityId()).isNull(),
                    () -> assertThat(result.getBody()).isNotNull()
            );
        }

        @Test
        @DisplayName("Should not map NoteEvent to NoteResponseEvent with null value")
        void shouldNotMapNoteEventToNoteResponseEventWithNullValue() {
            // When & Then
            assertThat(NoteMapper.mapToNoteResponseEvent(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Map to NoteResponse Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle Note with very long strings")
        void shouldHandleNoteWithVeryLongStrings() {
            // Given
            String longString = "a".repeat(1000);
            Note noteWithLongStrings = Note.builder()
                    .id("test-id")
                    .title(longString)
                    .content(longString)
                    .category(longString)
                    .important(true)
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .tags(longString + ", " + longString + "2")
                    .build();

            // When
            NoteResponse result = NoteMapper.mapToNoteResponse(noteWithLongStrings);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponse mapping with long strings",
                    () -> assertThat(result.getTitle()).hasSize(1000),
                    () -> assertThat(result.getContent()).hasSize(1000),
                    () -> assertThat(result.getCategory()).hasSize(1000),
                    () -> assertThat(result.getTags()).hasSize(2003)
            );
        }

        @Test
        @DisplayName("Should handle Note with special characters")
        void shouldHandleNoteWithSpecialCharacters() {
            // Given
            String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
            Note noteWithSpecialChars = Note.builder()
                    .id("test-id")
                    .title(specialChars)
                    .content(specialChars)
                    .category(specialChars)
                    .important(false)
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .tags(specialChars)
                    .build();

            // When
            NoteResponse result = NoteMapper.mapToNoteResponse(noteWithSpecialChars);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponse mapping with special characters",
                    () -> assertThat(result.getTitle()).isEqualTo(specialChars),
                    () -> assertThat(result.getContent()).isEqualTo(specialChars),
                    () -> assertThat(result.getCategory()).isEqualTo(specialChars),
                    () -> assertThat(result.getTags()).isEqualTo(specialChars)
            );
        }

        @Test
        @DisplayName("Should handle Note with Unicode characters")
        void shouldHandleNoteWithUnicodeCharacters() {
            // Given
            String unicodeChars = "测试 🚀 ñáéíóú αβγδε";
            Note noteWithUnicode = Note.builder()
                    .id("test-id")
                    .title(unicodeChars)
                    .content(unicodeChars)
                    .category(unicodeChars)
                    .important(true)
                    .createdAt(testDateTime)
                    .updatedAt(testDateTime)
                    .tags(unicodeChars)
                    .build();

            // When
            NoteResponse result = NoteMapper.mapToNoteResponse(noteWithUnicode);

            // Then
            assertThat(result).isNotNull();
            assertAll("NoteResponse mapping with Unicode characters",
                    () -> assertThat(result.getTitle()).isEqualTo(unicodeChars),
                    () -> assertThat(result.getContent()).isEqualTo(unicodeChars),
                    () -> assertThat(result.getCategory()).isEqualTo(unicodeChars),
                    () -> assertThat(result.getTags()).isEqualTo(unicodeChars)
            );
        }
    }
}
