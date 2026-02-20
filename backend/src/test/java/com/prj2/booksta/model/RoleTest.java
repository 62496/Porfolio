package com.prj2.booksta.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void testGettersAndSetters() {
        Role role = new Role();
        Long id = 1L;
        String name = "ROLE_ADMIN";
        Collection<User> users = new ArrayList<>();
        Collection<Privilege> privileges = new ArrayList<>();

        role.setId(id);
        role.setName(name);
        role.setUsers(users);
        role.setPrivileges(privileges);

        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getName()).isEqualTo(name);
        assertThat(role.getUsers()).isEqualTo(users);
        assertThat(role.getPrivileges()).isEqualTo(privileges);
    }

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        String name = "ROLE_USER";
        Collection<User> users = new ArrayList<>();
        Collection<Privilege> privileges = new ArrayList<>();

        Role role = new Role(id, name, users, privileges);

        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getName()).isEqualTo(name);
        assertThat(role.getUsers()).isEqualTo(users);
        assertThat(role.getPrivileges()).isEqualTo(privileges);
    }

    @Test
    void testEquals() {
        Role role1 = new Role();
        role1.setName("ROLE_ADMIN");
        role1.setId(1L);

        Role role2 = new Role();
        role2.setName("ROLE_ADMIN");
        role2.setId(2L); 

        Role role3 = new Role();
        role3.setName("ROLE_USER");
        role3.setId(1L);

        assertThat(role1).isEqualTo(role1);
        assertThat(role1).isEqualTo(role2); 
        assertThat(role1).isNotEqualTo(role3);
        assertThat(role1).isNotEqualTo(null);
        assertThat(role1).isNotEqualTo(new Object());
    }

    @Test
    void testHashCode() {
        Role role1 = new Role();
        role1.setName("ROLE_ADMIN");

        Role role2 = new Role();
        role2.setName("ROLE_ADMIN");

        Role role3 = new Role();
        role3.setName("ROLE_USER");

        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        assertThat(role1.hashCode()).isNotEqualTo(role3.hashCode());
    }
}