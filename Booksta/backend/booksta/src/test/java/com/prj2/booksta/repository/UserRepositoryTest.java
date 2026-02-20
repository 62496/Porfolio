package com.prj2.booksta.repository;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("New");
        user.setLastName("User");

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("user@test.com");

        assertTrue(found.isPresent());
        assertEquals("user@test.com", found.get().getEmail());
    }

    @Test
    void testFindByGoogleId() {
        User user = new User();
        user.setEmail("google@test.com");
        user.setGoogleId("123-GOOGLE-ID");
        user.setFirstName("Google");
        user.setLastName("Test");

        userRepository.save(user);

        Optional<User> found = userRepository.findByGoogleId("123-GOOGLE-ID");

        assertTrue(found.isPresent());
        assertEquals("123-GOOGLE-ID", found.get().getGoogleId());
    }

    @Test
    void testFindByIdWithFavorites() {
        Book book = new Book();
        book.setIsbn("978-1-2345-6789-0");
        book.setTitle("Livre Simple");
        book.setPublishingYear(2023);
        book.setDescription("Une description");

        book = bookRepository.save(book);

        User user = new User();
        user.setEmail("reader@test.com");
        user.setFirstName("Reader");
        user.setLastName("One");
        user.setFavoriteList(new HashSet<>());

        user.getFavoriteList().add(book);
        user = userRepository.save(user);

        Optional<User> found = userRepository.findByIdWithFavorites(user.getId());

        assertTrue(found.isPresent());
        assertFalse(found.get().getFavoriteList().isEmpty());
        assertEquals("Livre Simple", found.get().getFavoriteList().iterator().next().getTitle());
    }
}