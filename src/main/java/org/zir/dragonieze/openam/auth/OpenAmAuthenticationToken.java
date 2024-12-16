package org.zir.dragonieze.openam.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class OpenAmAuthenticationToken extends AbstractAuthenticationToken {
    private final OpenAmUserPrincipal user;

    public OpenAmAuthenticationToken(OpenAmUserPrincipal user, Role[] roles) {
        super(Arrays.stream(roles)
                .map(r -> new SimpleGrantedAuthority(r.getFullName()))
                .collect(Collectors.toUnmodifiableSet())
        );
        this.user = user;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}