package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.ConstraintViolation;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorAndGetters() {
        User user = new User(
                1L,
                "John",
                "Doe",
                "john@test.com",
                "test",
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                "google-123",
                "https://p.com/img.jpg");

        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@test.com", user.getEmail());
        assertEquals("test", user.getPassword());
        assertEquals("google-123", user.getGoogleId());
        assertEquals("https://p.com/img.jpg", user.getPicture());

        assertNotNull(user.getFavoriteList());
        assertNotNull(user.getFollowedAuthors());
        assertNotNull(user.getFollowedSeries());
        assertNotNull(user.getOwnedBooks());
        assertNotNull(user.getRoles());
    }

    @Test
    void testDefaultCollectionsAreEmpty() {
        User user = new User();
        assertNotNull(user.getFavoriteList(), "La liste ne doit pas être null");
        assertTrue(user.getFavoriteList().isEmpty());

        assertNotNull(user.getOwnedBooks(), "La liste ownedBooks ne doit pas être null");
        assertTrue(user.getOwnedBooks().isEmpty());

        assertTrue(user.getFollowedAuthors().isEmpty());
        assertTrue(user.getFollowedSeries().isEmpty());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testNotBlankValidation() {
        User user = new User();
        user.setFirstName("");
        user.setLastName("");
        user.setEmail("valid@mail.com");
        user.setPassword("secret");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size());

        for (ConstraintViolation<User> violation : violations) {
            String field = violation.getPropertyPath().toString();
            assertTrue(field.equals("firstName") || field.equals("lastName"));
            assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }

    @Test
    void testEmailValidation() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("invalid-email");
        user.setPassword("secret");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals(Email.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testAddFavoriteBook() {
        User user = new User();
        Book book = new Book();

        user.getFavoriteList().add(book);

        assertEquals(1, user.getFavoriteList().size());
        assertTrue(user.getFavoriteList().contains(book));
    }

   @Test
    void testEqualsAndHashCodeExcludeCollections() {
        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("a@a.com");
        u1.setFirstName("A");
        u1.setLastName("B");

        User u2 = new User();
        u2.setId(1L);
        u2.setEmail("a@a.com");
        u2.setFirstName("A");
        u2.setLastName("B");

        u2.getFavoriteList().add(new Book());
        assertEquals(u1, u2, "Les utilisateurs devraient être égaux même si favoriteList diffère (car exclu)");
        assertEquals(u1.hashCode(), u2.hashCode());

        u2.getOwnedBooks().add(new Book());
        assertEquals(u1, u2, "Les utilisateurs DEVRAIENT être égaux car ownedBooks EST exclu du hashcode");
        assertEquals(u1.hashCode(), u2.hashCode());
        u2.setEmail("b@b.com");
        assertNotEquals(u1, u2, "Les utilisateurs doivent être différents si l'email change");
    }

    @Test
    void testToStringExcludesCollections() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        user.getFavoriteList().add(new Book());

        String str = user.toString();

        System.out.println("ToString result: " + str);

        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("email=test@test.com"));

        assertFalse(str.contains("favoriteList="), "favoriteList devrait être exclu du toString");
        assertFalse(str.contains("followedAuthors="));
        assertFalse(str.contains("followedSeries="));

        assertTrue(str.contains("ownedBooks="), "ownedBooks n'est pas exclu, il doit apparaître");
        assertTrue(str.contains("roles="), "roles n'est pas exclu, il doit apparaître");
    }
}