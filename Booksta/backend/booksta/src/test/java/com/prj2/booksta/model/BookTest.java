package com.prj2.booksta.model;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void testBookConstructorAndGetters() {
        Book book = new Book(
                "9783161484100",  
                "Test Title",      
                2020,              // publishingYear
                "Description",     // description
                new HashSet<>(),   // authors
                new HashSet<>(),   // subjects
                null,        // series
                null,              // image
                null               // pages
        );

        assertEquals("9783161484100", book.getIsbn());
        assertEquals("Test Title", book.getTitle());
        assertEquals(2020, book.getPublishingYear());
        assertEquals("Description", book.getDescription());
        assertNotNull(book.getAuthors());
        assertNotNull(book.getSubjects());
        assertNull(book.getImage());
        assertNull(book.getPages());
    }

    @Test
    void testEqualsAndHashCodeIgnoreRelations() {
        Book b1 = new Book(
                "9783161484100",
                "Same",
                2020,
                "Desc",
                new HashSet<>(),
                new HashSet<>(),
                null,
                null,
                null
        );

        Book b2 = new Book(
                "9783161484100",
                "Same",
                2020,
                "Desc",
                null,    // authors ignored in equals()
                null,    // subjects ignored in equals()
                null,
                null,
                null
        );

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }
}
