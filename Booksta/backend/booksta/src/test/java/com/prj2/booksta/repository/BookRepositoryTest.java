package com.prj2.booksta.repository;


import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prj2.booksta.model.Book;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Author testAuthor;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        // Create test author - use unique suffix to avoid conflicts
        testAuthor = new Author();
        testAuthor.setFirstName("TestFirst");
        testAuthor.setLastName("TestAuthorLastName");
        entityManager.persist(testAuthor);

        // Create test subject with unique name to avoid conflicts with data-h2.sql
        testSubject = new Subject();
        testSubject.setName("UniqueTestSubject" + System.nanoTime());
        entityManager.persist(testSubject);

        // Create test book with unique ISBN
        Book book = new Book();
        book.setIsbn("TEST-ISBN-" + System.nanoTime());
        book.setTitle("Test Book Title For Search");
        book.setPublishingYear(1997);
        book.setDescription("A test book description");
        book.setAuthors(new HashSet<>());
        book.getAuthors().add(testAuthor);
        book.setSubjects(new HashSet<>());
        book.getSubjects().add(testSubject);
        book.setPages(309L);
        entityManager.persist(book);

        entityManager.flush();
    }

    @Test
    void testSearchBooksFindsResults() {
        List<Book> books = bookRepository.searchBooks("Test Book Title", null, null, null);

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().contains("Test Book Title")));
    }

    @Test
    void testSearchBooksReturnsEmptyList() {
        List<Book> books = bookRepository.searchBooks("TitleThatDoesNotExist", null, null, null);

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void testSearchBooksByAuthor() {
        List<Book> books = bookRepository.searchBooks(null, "TestAuthorLastName", null, null);

        assertNotNull(books);
        assertFalse(books.isEmpty());
    }
}
