package com.prj2.booksta.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleRoleHierarchyTest {

    @Test
    void testHierarchy_ShouldIncludeDirectChildRole() {
        SimpleRoleHierarchy hierarchy = new SimpleRoleHierarchy();
        hierarchy.add("ROLE_ADMIN", "ROLE_USER");

        List<GrantedAuthority> inputAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Collection<? extends GrantedAuthority> result = hierarchy.getReachableGrantedAuthorities(inputAuthorities);

        assertEquals(2, result.size());
        assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testHierarchy_ShouldIncludeTransitiveRoles() {
        SimpleRoleHierarchy hierarchy = new SimpleRoleHierarchy();
        hierarchy.add("ROLE_ROOT", "ROLE_ADMIN");
        hierarchy.add("ROLE_ADMIN", "ROLE_USER");

        List<GrantedAuthority> inputAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ROOT"));

        Collection<? extends GrantedAuthority> result = hierarchy.getReachableGrantedAuthorities(inputAuthorities);

        assertEquals(3, result.size());
        assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_ROOT")));
        assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testHierarchy_ShouldReturnOriginalIfNoHierarchy() {
        SimpleRoleHierarchy hierarchy = new SimpleRoleHierarchy();
        hierarchy.add("ROLE_ADMIN", "ROLE_USER");

        List<GrantedAuthority> inputAuthorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));

        Collection<? extends GrantedAuthority> result = hierarchy.getReachableGrantedAuthorities(inputAuthorities);

        assertEquals(1, result.size());
        assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_GUEST")));
    }
}