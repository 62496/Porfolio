package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

@SpringBootTest
public class PrivilegeTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorAndGetters() {
        Collection<Role> roles = new ArrayList<>();

        Privilege privilege = new Privilege(
                1L,
                "READ_PRIVILEGE",
                roles);

        assertEquals(1L, privilege.getId());
        assertEquals("READ_PRIVILEGE", privilege.getName());
        assertEquals(roles, privilege.getRoles());
        assertNotNull(privilege.getRoles());
    }

    @Test
    void testSettersAndNoArgsConstructor() {
        Privilege privilege = new Privilege();

        privilege.setId(10L);
        privilege.setName("WRITE_PRIVILEGE");
        privilege.setRoles(new HashSet<>());

        assertEquals(10L, privilege.getId());
        assertEquals("WRITE_PRIVILEGE", privilege.getName());
        assertNotNull(privilege.getRoles());
    }

    @Test
    void testRolesCollectionHandling() {
        Privilege privilege = new Privilege();
        privilege.setRoles(new HashSet<>());

        Role role = new Role();

        privilege.getRoles().add(role);

        assertEquals(1, privilege.getRoles().size());
        assertTrue(privilege.getRoles().contains(role));
    }

    @Test
    void testValidation() {
        Privilege privilege = new Privilege();
        privilege.setName("DELETE_PRIVILEGE");

        Set<ConstraintViolation<Privilege>> violations = validator.validate(privilege);
        assertTrue(violations.isEmpty(), "Le privilège devrait être valide");
    }
}