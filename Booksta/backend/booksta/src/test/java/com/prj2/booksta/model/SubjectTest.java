package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;

public class SubjectTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorAndGetters() {
        Set<Book> books = new HashSet<>();
        Subject subject = new Subject(1L, "Science", books);

        assertEquals(1L, subject.getId());
        assertEquals("Science", subject.getName());
        assertEquals(books, subject.getBooks());
    }

    @Test
    void testDefaultBooksIsEmpty() {
        Subject subject = new Subject();

        assertNotNull(subject.getBooks(), "La liste ne doit pas être null (initialisée par défaut)");
        assertTrue(subject.getBooks().isEmpty(), "La liste doit être vide par défaut");
    }

    @Test
    void testAddBook() {
        Subject subject = new Subject();

        Book book = new Book();
        subject.getBooks().add(book);

        assertEquals(1, subject.getBooks().size());
        assertTrue(subject.getBooks().contains(book));
    }

    @Test
    void testEqualsAndHashCodeExcludesBooks() {
        Subject s1 = new Subject();
        s1.setId(1L);
        s1.getBooks().add(new Book());

        Subject s2 = new Subject();
        s2.setId(1L);
        s2.getBooks().add(new Book());

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testToStringExcludesBooks() {
        Subject subject = new Subject();
        subject.setId(10L);
        subject.getBooks().add(new Book());

        String str = subject.toString();

        assertTrue(str.contains("10"));
        assertFalse(str.contains("books"));
    }

    @Test
    void testNotNullValidation() {
        Subject subject = new Subject();
        subject.setName(null);

        Set<ConstraintViolation<Subject>> violations = validator.validate(subject);
        assertEquals(1, violations.size());

        ConstraintViolation<Subject> violation = violations.iterator().next();
        String field = violation.getPropertyPath().toString();
        Class<? extends Annotation> annotation = violation.getConstraintDescriptor().getAnnotation().annotationType();

        assertEquals("name", field);
        assertEquals(NotNull.class, annotation);
    }
}