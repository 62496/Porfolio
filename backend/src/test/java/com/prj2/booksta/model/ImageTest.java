package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

@SpringBootTest
public class ImageTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorAndGetters() {
        Image image = new Image(
                1L,
                "http://example.com/image.png");

        assertEquals(1L, image.getId());
        assertEquals("http://example.com/image.png", image.getUrl());
    }

    @Test
    void testCustomConstructor() {
        Image image = new Image(100L);

        assertEquals(100L, image.getId());
        assertNull(image.getUrl(), "L'URL doit être nulle via ce constructeur");
    }

    @Test
    void testSettersAndNoArgsConstructor() {
        Image image = new Image();
        image.setId(50L);
        image.setUrl("https://test.com/photo.jpg");

        assertEquals(50L, image.getId());
        assertEquals("https://test.com/photo.jpg", image.getUrl());
    }

    @Test
    void testValidation() {
        Image image = new Image();
        image.setUrl("valid-url");

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertTrue(violations.isEmpty(), "L'image ne devrait pas avoir d'erreurs de validation");
    }
}
