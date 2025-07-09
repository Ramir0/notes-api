package dev.amir.notes.notes.application.mappers;

import dev.amir.notes.notes.application.events.NoteResponseEvent;
import dev.amir.notes.notes.application.responses.NoteResponse;
import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.infrastructure.data.events.NoteEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Mapper class to convert Note entities to NoteResponse DTOs and NoteEvent to NoteResponseEvent.
 * <p>
 * This class provides static methods to facilitate the conversion process.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoteMapper {
    /**
     * Helper method to map Note entity to NoteResponse DTO
     */
    public static NoteResponseEvent mapToNoteResponseEvent(NoteEvent event) {
        return event != null
                ? new NoteResponseEvent(event.getEventType(), mapToNoteResponse(event.getBody()), event.getEntityId())
                : null;

    }

    /**
     * Helper method to map Note entity to NoteResponse DTO
     */
    public static NoteResponse mapToNoteResponse(Note note) {
        return note != null
                ? NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .category(note.getCategory())
                .important(note.getImportant())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .tags(note.getTags())
                .build()
                : null;
    }
}
