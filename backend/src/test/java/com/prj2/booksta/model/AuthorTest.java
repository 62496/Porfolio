package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;

public class AuthorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorAndGetters() {
        Image image = new Image();

        Author author = new Author(
                1L,
                "John",
                "Doe",
                new HashSet<>(),
                new HashSet<>(),
                image,
                null);

        assertEquals(1L, author.getId());
        assertEquals("John", author.getFirstName());
        assertEquals("Doe", author.getLastName());

        assertEquals(image, author.getImage());

        assertNotNull(author.getBooks());
        assertNotNull(author.getFollowers());
        assertTrue(author.getBooks().isEmpty());
    }

    @Test
    void testDefaultCollectionsAreEmpty() {
        Author author = new Author();
        assertNotNull(author.getBooks());
        assertNotNull(author.getFollowers());
        assertTrue(author.getBooks().isEmpty());
    }

    @Test
    void testAddBook() {
        Author author = new Author();
        Book book = new Book();

        author.getBooks().add(book);

        assertEquals(1, author.getBooks().size());
        assertTrue(author.getBooks().contains(book));
    }

    @Test
    void testEqualsAndHashCodeExcludesCollections() {
        Author a1 = new Author();
        a1.setId(1L);
        a1.setFirstName("John");
        a1.setLastName("Doe");

        Author a2 = new Author();
        a2.setId(1L);
        a2.setFirstName("John");
        a2.setLastName("Doe");

        a2.getBooks().add(new Book());
        a2.getFollowers().add(new User());

        assertEquals(a1, a2, "Les auteurs devraient être égaux même si leurs livres/followers diffèrent");
        assertEquals(a1.hashCode(), a2.hashCode(), "Le hashCode doit être identique");

        a2.setLastName("Smith");
        assertNotEquals(a1, a2);
    }

    @Test
    void testToStringExcludesCollections() {
        Author author = new Author();
        author.setId(10L);
        author.getBooks().add(new Book());

        String str = author.toString();

        assertTrue(str.contains("10"));
        assertFalse(str.contains("books="));
        assertFalse(str.contains("followers="));
    }

    @Test
    void testNotBlankValidation() {
        Author author = new Author();
        author.setFirstName("");
        author.setLastName("");

        Set<ConstraintViolation<Author>> violations = validator.validate(author);

        assertEquals(2, violations.size());

        for (ConstraintViolation<Author> violation : violations) {
            String field = violation.getPropertyPath().toString();
            assertTrue(field.equals("firstName") || field.equals("lastName"));
            assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }
}