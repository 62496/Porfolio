package com.prj2.booksta.config;

import com.prj2.booksta.model.Role;
import com.prj2.booksta.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
    }

    private void initRoles() {
        List<String> roleNames = Arrays.asList("USER", "AUTHOR", "LIBRARIAN", "SELLER", "ADMIN");

        for (String roleName : roleNames) {
            if (roleRepository.findByName(roleName) == null) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("Created role: " + roleName);
            }
        }
    }
}
