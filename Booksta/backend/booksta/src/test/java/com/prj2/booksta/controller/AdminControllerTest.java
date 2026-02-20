package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.Role;
import com.prj2.booksta.model.User;
import com.prj2.booksta.service.RoleService;
import com.prj2.booksta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

        role = new Role();
        role.setId(1L);
        role.setName("USER");

        user = new User();
        user.setId(1L);
        user.setEmail("test@admin.com");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("test@admin.com")));

        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserById_Found() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/admin/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@admin.com")));

        verify(userService).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(99L);
    }

    @Test
    void testGetAllRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Collections.singletonList(role));

        mockMvc.perform(get("/api/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("USER")));

        verify(roleService).getAllRoles();
    }

    @Test
    void testAddRoleToUser_Success() throws Exception {
        doNothing().when(userService).addRoleToUser(1L, "ADMIN");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/admin/users/{userId}/roles/{roleName}", 1L, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@admin.com")));

        verify(userService).addRoleToUser(1L, "ADMIN");
    }

    @Test
    void testAddRoleToUser_UserNotFoundAfterAdd() throws Exception {
        doNothing().when(userService).addRoleToUser(1L, "ADMIN");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/admin/users/{userId}/roles/{roleName}", 1L, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddRoleToUser_Exception() throws Exception {
        doThrow(new IllegalArgumentException("Role not found")).when(userService).addRoleToUser(1L, "INVALID");

        mockMvc.perform(post("/api/admin/users/{userId}/roles/{roleName}", 1L, "INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Role not found"));
    }

    @Test
    void testRemoveRoleFromUser_Success() throws Exception {
        doNothing().when(userService).removeRoleFromUser(1L, "USER");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/admin/users/{userId}/roles/{roleName}", 1L, "USER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@admin.com")));

        verify(userService).removeRoleFromUser(1L, "USER");
    }

    @Test
    void testRemoveRoleFromUser_UserNotFoundAfterRemove() throws Exception {
        doNothing().when(userService).removeRoleFromUser(1L, "USER");
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/users/{userId}/roles/{roleName}", 1L, "USER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveRoleFromUser_Exception() throws Exception {
        doThrow(new IllegalArgumentException("User does not have role")).when(userService).removeRoleFromUser(1L, "ADMIN");

        mockMvc.perform(delete("/api/admin/users/{userId}/roles/{roleName}", 1L, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User does not have role"));
    }
}