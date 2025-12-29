package com.prj2.booksta.controller;

import com.prj2.booksta.model.Role;
import com.prj2.booksta.model.dto.UserAdminResponse;
import com.prj2.booksta.service.RoleService;
import com.prj2.booksta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * Get all users with their roles
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponse>> getAllUsers() {
        List<UserAdminResponse> users = userService.getAllUsers().stream()
                .map(UserAdminResponse::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Get a specific user by ID with their roles
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserAdminResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(UserAdminResponse.fromUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all available roles
     */
    @GetMapping("/roles")
    public ResponseEntity<List<UserAdminResponse.RoleInfo>> getAllRoles() {
        List<UserAdminResponse.RoleInfo> roles = roleService.getAllRoles().stream()
                .map(role -> new UserAdminResponse.RoleInfo(role.getId(), role.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    /**
     * Add a role to a user
     */
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<?> addRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        try {
            userService.addRoleToUser(userId, roleName);
            return userService.getUserById(userId)
                    .map(user -> ResponseEntity.ok(UserAdminResponse.fromUser(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove a role from a user
     */
    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<?> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        try {
            userService.removeRoleFromUser(userId, roleName);
            return userService.getUserById(userId)
                    .map(user -> ResponseEntity.ok(UserAdminResponse.fromUser(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
