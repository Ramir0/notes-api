package dev.amir.notes.notes.infrastructure.data.events;

import dev.amir.notes.notes.domain.entities.Note;
import dev.amir.notes.notes.domain.events.AbstractEvent;
import dev.amir.notes.notes.domain.events.EventType;

/**
 * Represents an event related to Note entities.
 * <p>
 * This class extends AbstractEvent to provide specific functionality for Note events.
 * It includes constructors for creating events with a body or just an entity ID.
 */
public class NoteEvent extends AbstractEvent<Note> {
    /**
     * Constructs a NoteEvent with the specified event type and body.
     *
     * @param eventType the type of the event
     * @param body      the Note entity associated with the event
     */
    public NoteEvent(EventType eventType, Note body) {
        super(eventType, body);
    }

    /**
     * Constructs a NoteEvent with the specified event type and entity ID.
     *
     * @param eventType the type of the event
     * @param entityId  the ID of the Note entity associated with the event
     */
    public NoteEvent(EventType eventType, String entityId) {
        super(eventType, entityId);
    }

    @Override
    protected String extractEntityId(Note body) {
        return body.getId();
    }
}
