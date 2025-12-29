package com.prj2.booksta.repository;

import java.util.List;
import java.util.Optional;

import com.prj2.booksta.model.dto.BookWithLatestReadingEvent;
import com.prj2.booksta.repository.projections.BookWithLatestReadingEventView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prj2.booksta.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    public Optional<User> findByGoogleId(String googleId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favoriteList WHERE u.id = :id")
    Optional<User> findByIdWithFavorites(@Param("id") Long id);

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.ownedBooks
    WHERE u.id = :id
""")
    Optional<User> findByIdWithOwnedBooks(@Param("id") Long id);

    /**
     * Recherche générique par nom/prénom/email
     */
    @Query("""
        SELECT u FROM User u
        WHERE
            LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<User> searchByNameOrEmail(@Param("query") String query);

    @Query("""
        SELECT u FROM User u
        WHERE u.googleId IS NOT NULL
          AND (:excludeId IS NULL OR u.id <> :excludeId)
          AND (
                :query IS NULL
             OR :query = ''
             OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
          )
    """)
    List<User> searchGoogleUsers(@Param("query") String query,
            @Param("excludeId") Long excludeId);

    @Query("""
        SELECT
            b.isbn AS isbn,
            b.title AS title,
            b.publishingYear AS publishingYear,
            b.pages AS pages,
            b.authors AS authors,
            b.image.url AS imageUrl,
            e.readingEvent AS latestEventType,
            e.occurredAt AS latestEventOccurredAt
        FROM User u
        JOIN u.ownedBooks b
        LEFT JOIN BookReadEvent e
            ON e.book = b
            AND e.user = u
            AND e.occurredAt = (
                SELECT MAX(e2.occurredAt)
                FROM BookReadEvent e2
                WHERE e2.book = b
                  AND e2.user = u
            )
        WHERE u.id = :userId
    """)
    List<BookWithLatestReadingEventView> findOwnedBooksWithLatestReadingEventView(
            @Param("userId") Long userId
    );

    @Query("""
    SELECT
        b.isbn AS isbn,
        b.title AS title,
        b.publishingYear AS publishingYear,
        b.pages AS pages,
        b.image.url AS imageUrl,
        e.readingEvent AS latestEventType,
        e.occurredAt AS latestEventOccurredAt
    FROM User u
    JOIN u.ownedBooks b
    JOIN BookReadEvent e
        ON e.book = b
        AND e.user = u
        AND e.occurredAt = (
            SELECT MAX(e2.occurredAt)
            FROM BookReadEvent e2
            WHERE e2.book = b
              AND e2.user = u
        )
    WHERE u.id = :userId
""")
    List<BookWithLatestReadingEventView> findOwnedBooksWithReadingEvent(
            @Param("userId") Long userId
    );

    /**
     * Check if any user has a specific role
     */
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE r.name = :roleName")
    boolean existsUserWithRole(@Param("roleName") String roleName);
}
