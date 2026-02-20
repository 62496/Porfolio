package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class SeriesTest {

    @Test
    void testDefaultConstructorAndCollections() {
        Series series = new Series();

        assertNotNull(series.getBooks(), "La liste des livres ne doit pas être null");
        assertNotNull(series.getFollowers(), "La liste des followers ne doit pas être null");
        assertTrue(series.getBooks().isEmpty());
        assertTrue(series.getFollowers().isEmpty());
        assertNull(series.getId());
        assertNull(series.getTitle());
        assertNull(series.getDescription());
        assertNull(series.getAuthor());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Long id = 1L;
        String title = "Harry Potter";
        String description = "A story about a wizard.";
        Author author = new Author();
        author.setId(10L);
        Set<Book> books = new LinkedHashSet<>();
        Set<User> followers = new HashSet<>();
        Series series = new Series(id, title, description, author, books, followers);

        assertEquals(1L, series.getId());
        assertEquals("Harry Potter", series.getTitle());
        assertEquals("A story about a wizard.", series.getDescription());
        assertEquals(author, series.getAuthor());
        assertEquals(books, series.getBooks());
        assertEquals(followers, series.getFollowers());
    }

    @Test
    void testSetters() {
        Series series = new Series();
        Author author = new Author();

        series.setId(5L);
        series.setTitle("Dune");
        series.setDescription("Sci-fi masterpiece");
        series.setAuthor(author);

        assertEquals(5L, series.getId());
        assertEquals("Dune", series.getTitle());
        assertEquals("Sci-fi masterpiece", series.getDescription());
        assertEquals(author, series.getAuthor());
    }

    @Test
    void testEqualsAndHashCodeExcludesRelations() {
        Series s1 = new Series();
        s1.setId(1L);
        s1.setTitle("Foundation");
        s1.setDescription("Asimov books");

        Series s2 = new Series();
        s2.setId(1L);
        s2.setTitle("Foundation");
        s2.setDescription("Asimov books");
        s2.getBooks().add(new Book());
        s2.getFollowers().add(new User());

        Author commonAuthor = new Author();
        s1.setAuthor(commonAuthor);
        s2.setAuthor(commonAuthor);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());

        s2.setTitle("Foundation & Empire");
        assertNotEquals(s1, s2);
    }

    @Test
    void testToStringExcludesRelations() {
        Series series = new Series();
        series.setId(10L);
        series.setTitle("Lord of the Rings");
        series.setDescription("Epic fantasy");
        series.getBooks().add(new Book());
        series.getFollowers().add(new User());

        String result = series.toString();

        assertTrue(result.contains("Lord of the Rings"));
        assertTrue(result.contains("Epic fantasy"));
        assertFalse(result.contains("books=" + series.getBooks()));

    }
}