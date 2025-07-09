package dev.amir.notes.notes.application.events;

import dev.amir.notes.notes.application.responses.NoteResponse;
import dev.amir.notes.notes.domain.events.AbstractEvent;
import dev.amir.notes.notes.domain.events.EventType;

/**
 * Represents an event related to NoteResponse entities.
 * <p>
 * This class extends {@link AbstractEvent} to provide specific functionality for {@link NoteResponse} events.
 * It includes constructors for creating events with a body or just an entity ID.
 */
public class NoteResponseEvent extends AbstractEvent<NoteResponse> {
    public NoteResponseEvent(EventType eventType, NoteResponse body, String entityId) {
        super(eventType, body, entityId);
    }

    @Override
    protected String extractEntityId(NoteResponse body) {
        return body.getId();
    }
}
