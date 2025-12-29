package com.prj2.booksta.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class SimpleRoleHierarchy implements RoleHierarchy {

    private final Map<String, Set<String>> hierarchyMap = new HashMap<>();

    public SimpleRoleHierarchy add(String higher, String lower) {
        hierarchyMap.computeIfAbsent(higher, k -> new HashSet<>()).add(lower);
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities) {

        Set<GrantedAuthority> expanded = new HashSet<>(authorities);

        boolean changed;

        do {
            changed = false;

            for (GrantedAuthority auth : new HashSet<>(expanded)) {
                String role = auth.getAuthority();

                Set<String> implied = hierarchyMap.get(role);
                if (implied == null) continue;

                for (String child : implied) {
                    GrantedAuthority newAuth = new SimpleGrantedAuthority(child);

                    if (expanded.add(newAuth)) {
                        changed = true;
                    }
                }
            }
        } while (changed);

        return expanded;
    }
}
