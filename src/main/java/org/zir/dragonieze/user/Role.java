package org.zir.dragonieze.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    public static Optional<Role> byFullName(String fullName) {
        for (Role role : Role.values()) {
            if (role.getFullName().equalsIgnoreCase(fullName)) {
                return Optional.of(role);
            }
        }

        return Optional.empty();
    }

    private final String fullName;
}
