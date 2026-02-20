package com.prj2.booksta.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.prj2.booksta.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prj2.booksta.model.User;
import com.prj2.booksta.model.UserBookInventory;

@DataJpaTest
class UserBookInventoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserBookInventoryRepository inventoryRepository;

    @Test
    void testFindByUserId_Found() {
        User user = new User();
        user.setFirstName("Inventory");
        user.setLastName("Tester");
        user.setEmail("inventory@test.com");
        user.setPassword("password123");
        User savedUser = entityManager.persist(user);

        Book book = new Book();
        book.setIsbn("978-1234567890");
        book.setTitle("Test Book");

        book.setDescription("Ceci est une description de test obligatoire.");
        book.setPublishingYear(2024);

        Book savedBook = entityManager.persist(book);

        UserBookInventory inventory = new UserBookInventory();
        inventory.setUser(savedUser);
        inventory.setBook(savedBook);
        inventory.setQuantity(5L);

        entityManager.persist(inventory);
        entityManager.flush();

        List<UserBookInventory> results = inventoryRepository.findByUserId(savedUser.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getQuantity()).isEqualTo(5L);
        assertThat(results.get(0).getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void testFindByUserId_NotFound() {
        Long randomUserId = 99999L;
        List<UserBookInventory> results = inventoryRepository.findByUserId(randomUserId);

        assertThat(results).isEmpty();
    }
}