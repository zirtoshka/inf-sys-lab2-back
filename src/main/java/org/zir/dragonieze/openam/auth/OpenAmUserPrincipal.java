package org.zir.dragonieze.openam.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public class OpenAmUserPrincipal {
    User user;
    String authCookie;
    Role[] roles;

    public boolean hasRole(Role role) {
        return role != null && Arrays.asList(roles).contains(role);
    }
}
