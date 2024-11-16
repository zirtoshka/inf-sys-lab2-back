package org.zir.dragonieze.admin;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.Optional;

@Configuration
public class AdminInitializer {
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password")
    private String adminPassword;
    @Bean
    public ApplicationRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<User> admin = userRepository.findByRole(Role.ADMIN);
            if (admin.isEmpty()) {
                User newAdmin = new User();
                newAdmin.setUsername(adminUsername);
                newAdmin.setPassword(passwordEncoder.encode(adminPassword));
                newAdmin.setRole(Role.ADMIN);
                userRepository.save(newAdmin);
            }
        };
    }
}
