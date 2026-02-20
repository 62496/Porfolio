package com.prj2.booksta.service;

import com.prj2.booksta.model.Role;
import com.prj2.booksta.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void testGetRole() {
        String roleName = "ROLE_USER";
        Role role = new Role();
        role.setName(roleName);

        when(roleRepository.findByName(roleName)).thenReturn(role);

        Role result = roleService.getRole(roleName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(roleName);
        verify(roleRepository).findByName(roleName);
    }

    @Test
    void testGetAllRoles() {
        Role role1 = new Role();
        role1.setName("ROLE_ADMIN");
        Role role2 = new Role();
        role2.setName("ROLE_USER");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<Role> result = roleService.getAllRoles();

        assertThat(result).hasSize(2);
        assertThat(result).contains(role1, role2);
        verify(roleRepository).findAll();
    }

    @Test
    void testGetAllRoles_Empty() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        List<Role> result = roleService.getAllRoles();

        assertThat(result).isEmpty();
        verify(roleRepository).findAll();
    }

    @Test
    void testGetRoleById_Found() {
        Long id = 1L;
        Role role = new Role();
        role.setId(id);
        role.setName("ROLE_TEST");

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(roleRepository).findById(id);
    }

    @Test
    void testGetRoleById_NotFound() {
        Long id = 99L;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        Role result = roleService.getRoleById(id);

        assertThat(result).isNull();
        verify(roleRepository).findById(id);
    }
}