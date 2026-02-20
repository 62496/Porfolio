package com.prj2.booksta.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.User;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testFindByUser_Found() {
        User user = new User();
        user.setFirstName("Victor");
        user.setLastName("Hugo");
        user.setEmail("victor@hugo.com");
        User savedUser = entityManager.persist(user);

        Author author = new Author();
        author.setFirstName("Victor");
        author.setLastName("Hugo");
        author.setUser(savedUser);
        entityManager.persist(author);

        entityManager.flush();

        Optional<Author> result = authorRepository.findByUser(savedUser);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Victor");
        assertThat(result.get().getUser()).isEqualTo(savedUser);
    }

    @Test
    void testFindByUser_NotFound() {
        User simpleUser = new User();
        simpleUser.setFirstName("Simple");
        simpleUser.setLastName("User");
        simpleUser.setEmail("simple@user.com");
        
        User savedUser = entityManager.persistFlushFind(simpleUser);

        Optional<Author> result = authorRepository.findByUser(savedUser);

        assertThat(result).isEmpty();
    }
}