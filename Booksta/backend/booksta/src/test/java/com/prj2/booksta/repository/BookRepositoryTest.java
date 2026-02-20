package com.prj2.booksta.repository;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Subject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSearchBooksFindsResults() {
        Subject fantasySubject = entityManager.find(Subject.class, 1L);

        assertNotNull(fantasySubject, "Le sujet Fantasy (ID 1) devrait exister via data.sql");

        Author author = new Author();
        author.setFirstName("J.K.");
        author.setLastName("Rowling");
        entityManager.persist(author);

        Book book = new Book();
        book.setIsbn("978-0-12345-678-9");
        book.setTitle("Harry Potter and the Philosopher's Stone");
        book.setPublishingYear(1997);
        book.setDescription("Un livre sur la magie");

        book.setAuthors(new HashSet<>());
        book.setSubjects(new HashSet<>());

        book.getAuthors().add(author);
        book.getSubjects().add(fantasySubject);

        bookRepository.save(book);

        List<Book> books = bookRepository.searchBooks("Harry", null, null, null);

        assertNotNull(books);
        assertFalse(books.isEmpty(), "La liste ne doit pas être vide");
        assertEquals("Harry Potter and the Philosopher's Stone", books.get(0).getTitle());

        List<Book> fantasyBooks = bookRepository.searchBooks(null, null, "Fantasy", null);
        assertFalse(fantasyBooks.isEmpty());
    }

    @Test
    void testSearchBooksReturnsEmptyList() {
        List<Book> books = bookRepository.searchBooks("TitleThatDoesNotExist", null, null, null);

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }
}
