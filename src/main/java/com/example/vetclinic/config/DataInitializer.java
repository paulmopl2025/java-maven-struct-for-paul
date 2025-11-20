package com.example.vetclinic.config;

import com.example.vetclinic.domain.model.Role;
import com.example.vetclinic.domain.model.User;
import com.example.vetclinic.infrastructure.persistence.RoleJpaRepository;
import com.example.vetclinic.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserJpaRepository userRepo;
    private final RoleJpaRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Ensure roles exist (handled by data.sql, but good to be safe or if data.sql
        // fails)
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_VET");

        // Create Admin User
        if (!userRepo.existsByUsername("admin")) {
            Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .email("admin@vetclinic.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Collections.singleton(adminRole))
                    .build();
            userRepo.save(admin);
            System.out.println("Admin user created: admin / admin123");
        }
    }

    private void createRoleIfNotFound(String name) {
        if (roleRepo.findByName(name).isEmpty()) {
            roleRepo.save(Role.builder().name(name).build());
        }
    }
}
