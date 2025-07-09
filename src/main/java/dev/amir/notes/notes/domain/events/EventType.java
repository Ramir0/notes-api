package dev.amir.notes.notes.domain.events;

/**
 * Enum representing the types of events that can occur in the application.
 * <p>
 * This is used to categorize events for processing and handling.
 */
public enum EventType {
    /**
     * Initial event type, used to signify the start of an event stream.
     */
    INITIAL,
    /**
     * Event type for inserting new entities.
     */
    INSERT,
    /**
     * Event type for updating existing entities.
     */
    UPDATE,
    /**
     * Event type for deleting entities.
     */
    DELETE
}
