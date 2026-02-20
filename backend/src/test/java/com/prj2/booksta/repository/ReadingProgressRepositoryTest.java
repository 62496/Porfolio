package com.prj2.booksta.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.ReadingProgress;
import com.prj2.booksta.model.User;

@DataJpaTest
class ReadingProgressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReadingProgressRepository progressRepository;

    @Test
    void testFindByUserIdAndBookIsbn_Found() {
        User user = new User();
        user.setFirstName("Reader");
        user.setLastName("One");
        user.setEmail("reader1@test.com");
        User savedUser = entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("TEST-ISBN-001");
        book.setTitle("Test Book");

        book.setDescription("Une description valide pour le test.");
        book.setPublishingYear(2023);
        book.setPages(300L);
        entityManager.persist(book);

        ReadingProgress progress = new ReadingProgress();
        progress.setUser(savedUser);
        progress.setBook(book);

        progress.initializeTotalPages(300L);
        progress.setCurrentPage(50L);

        entityManager.persist(progress);
        entityManager.flush();

        Optional<ReadingProgress> result = progressRepository.findByUserIdAndBookIsbn(savedUser.getId(),
                "TEST-ISBN-001");

        assertThat(result).isPresent();
        assertThat(result.get().getCurrentPage()).isEqualTo(50L);
    }

    @Test
    void testFindByUserIdAndBookIsbn_NotFound() {
        Optional<ReadingProgress> result = progressRepository.findByUserIdAndBookIsbn(999L, "FAKE-ISBN");
        assertThat(result).isEmpty();
    }

    @Test
    void testFindByUserId_ReturnsList() {
        User user = new User();
        user.setFirstName("Reader");
        user.setLastName("Two");
        user.setEmail("reader2@test.com");
        User savedUser = entityManager.persist(user);

        Book b1 = new Book();
        b1.setIsbn("ISBN-A");
        b1.setTitle("A");
        b1.setDescription("Desc A");
        b1.setPublishingYear(2000);
        entityManager.persist(b1);

        Book b2 = new Book();
        b2.setIsbn("ISBN-B");
        b2.setTitle("B");
        b2.setDescription("Desc B");
        b2.setPublishingYear(2001);
        entityManager.persist(b2);

        ReadingProgress p1 = new ReadingProgress();
        p1.setUser(savedUser);
        p1.setBook(b1);
        p1.initializeTotalPages(100L);
        entityManager.persist(p1);

        ReadingProgress p2 = new ReadingProgress();
        p2.setUser(savedUser);
        p2.setBook(b2);
        p2.initializeTotalPages(200L);
        entityManager.persist(p2);

        entityManager.flush();

        List<ReadingProgress> results = progressRepository.findByUserId(savedUser.getId());

        assertThat(results).hasSize(2);
    }
}