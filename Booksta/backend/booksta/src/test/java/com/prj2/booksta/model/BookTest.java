package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

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
    void testDefaultCollectionsAreEmpty() {
        Book book = new Book();
        assertNotNull(book.getAuthors());
        assertTrue(book.getAuthors().isEmpty());
        assertNotNull(book.getSubjects());
        assertTrue(book.getSubjects().isEmpty());
    }

    @Test
    void testAddAuthorsAndSubjects() {
        Book book = new Book();
        Author author = new Author();
        Subject subject = new Subject();

        book.getAuthors().add(author);
        book.getSubjects().add(subject);

        assertEquals(1, book.getAuthors().size());
        assertTrue(book.getAuthors().contains(author));

        assertEquals(1, book.getSubjects().size());
        assertTrue(book.getSubjects().contains(subject));
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
                null);

        Book b2 = new Book(
                "9783161484100",
                "Same",
                2020,
                "Desc",
                null,
                null,
                null,
                null,
                null);

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    void testToStringExcludesCollections() {
        Book book = new Book();
        book.setIsbn("9783161484100");
        book.getAuthors().add(new Author());
        book.getSubjects().add(new Subject());

        String str = book.toString();
        assertTrue(str.contains("9783161484100"));
        assertFalse(str.contains("authors"));
        assertFalse(str.contains("subjects"));
    }

}
