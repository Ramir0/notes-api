package dev.amir.notes.notes.domain.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents an abstract event in the application.
 * <p>
 * This class serves as a base for specific event types, encapsulating common properties
 * such as event type, body, and entity ID.
 *
 * @param <T> the type of the event body
 */
@EqualsAndHashCode
@Getter
public abstract class AbstractEvent<T> {
    private final EventType eventType;
    private final T body;
    private final String entityId;

    /**
     * Constructs an AbstractEvent with the specified event type and body.
     *
     * @param eventType the type of the event
     * @param body      the body of the event
     */
    public AbstractEvent(EventType eventType, T body) {
        this.eventType = eventType;
        this.body = body;
        this.entityId = extractEntityId(body);
    }

    /**
     * Constructs an AbstractEvent with the specified event type and entity ID.
     *
     * @param eventType the type of the event
     * @param entityId  the ID of the entity associated with the event
     */
    public AbstractEvent(EventType eventType, String entityId) {
        this.eventType = eventType;
        this.body = null;
        this.entityId = entityId;
    }

    /**
     * Constructs an AbstractEvent with the specified event type, body, and entity ID.
     *
     * @param eventType the type of the event
     * @param body      the body of the event
     * @param entityId  the ID of the entity associated with the event
     */
    public AbstractEvent(EventType eventType, T body, String entityId) {
        this.eventType = eventType;
        this.body = body;
        this.entityId = entityId;
    }

    /**
     * Extracts the entity ID from the event body.
     * This method must be implemented by subclasses to provide specific logic
     * for extracting the entity ID from the body.
     *
     * @param body the body of the event
     * @return the extracted entity ID
     */
    protected abstract String extractEntityId(T body);
}
