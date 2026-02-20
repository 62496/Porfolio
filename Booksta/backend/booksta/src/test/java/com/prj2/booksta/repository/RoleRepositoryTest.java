package com.prj2.booksta.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.prj2.booksta.model.Role;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByName_Found() {
        Role foundRole = roleRepository.findByName("ADMIN");

        assertNotNull(foundRole, "Le rôle ADMIN devrait exister via data.sql");
        assertEquals("ADMIN", foundRole.getName());
    }

    @Test
    void testFindByName_NotFound() {
        Role foundRole = roleRepository.findByName("ROLE_INEXISTANT");
        assertNull(foundRole);
    }
}