package org.zir.dragonieze.admin;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zir.dragonieze.openam.api.OpenAmRestApiClient;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class AdminInitializer {
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.dn}")
    private String adminDn;

    @Bean
    public ApplicationRunner initAdmin(UserRepository userRepository, OpenAmRestApiClient openAmApi) {
        return args -> {
            Optional<User> amAdmin = userRepository.findByUsername(adminUsername);
            if (amAdmin.isEmpty()) {
                User newAdmin = User.builder()
                        .username(adminUsername)
                        .dn(adminDn)
                        .build();
                userRepository.save(newAdmin);
                amAdmin = Optional.of(newAdmin);
            }

            String cookie = openAmApi.authenticateUser(adminUsername, adminPassword);
            List<String> roles = Arrays.asList(openAmApi.getUserGroups(cookie, amAdmin.get().getDn()));
            if (roles.size() != Role.values().length) {
                for (Role role : Role.values()) {
                    if (roles.contains(role.getFullName())) {
                        continue;
                    }

                    openAmApi.addUserToGroup(cookie, amAdmin.get().getDn(), role.getFullName());
                }
            }
        };
    }
}
