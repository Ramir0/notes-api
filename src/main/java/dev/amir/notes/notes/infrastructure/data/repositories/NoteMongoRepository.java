package dev.amir.notes.notes.infrastructure.data.repositories;

import dev.amir.notes.notes.domain.entities.Note;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive MongoDB repository for Note entities.
 * <p>
 * Provides methods to perform CRUD operations and custom queries.
 */
@Repository
public interface NoteMongoRepository extends ReactiveMongoRepository<Note, String> {
    /**
     * Find notes by category.
     *
     * @param category the category to filter notes
     * @return a Flux of notes matching the category
     */
    Flux<Note> findByCategory(String category);

    /**
     * Find notes by importance.
     *
     * @param important the importance status to filter notes
     * @return a Flux of notes matching the importance status
     */
    Flux<Note> findByImportant(Boolean important);

    /**
     * Find notes by title containing a specific string (case-insensitive).
     *
     * @param title the title substring to search for
     * @return a Flux of notes matching the title substring
     */
    Flux<Note> findByTitleContainingIgnoreCase(String title);

    /**
     * Find notes by content containing a specific string (case-insensitive).
     *
     * @param content the content substring to search for
     * @return a Flux of notes matching the content substring
     */
    Flux<Note> findByContentContainingIgnoreCase(String content);

    /**
     * Find notes by tags containing a specific tag (case-insensitive).
     *
     * @param tag the tag substring to search for
     * @return a Flux of notes matching the tag substring
     */
    @Query("{ 'tags': { $regex: ?0, $options: 'i' } }")
    Flux<Note> findByTagsContaining(String tag);

    /**
     * Count notes by category.
     *
     * @param category the category to filter notes
     * @return a Mono containing the count of notes matching the category
     */
    Mono<Long> countByCategory(String category);
}
