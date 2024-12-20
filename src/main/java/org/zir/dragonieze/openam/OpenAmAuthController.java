package org.zir.dragonieze.openam;


import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dto.UserDTO;
import org.zir.dragonieze.openam.api.OpenAmRestApiClient;
import org.zir.dragonieze.openam.auth.OpenAmAuthenticationFilter;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.user.Role;

import java.util.Arrays;

@RestController
@RequestMapping("/dragon/am")
public class OpenAmAuthController {
    @Setter(onMethod_ = {@Autowired})
    private OpenAmRestApiClient openAmApi;
    private OpenAmAuthenticationFilter openAmAuthenticationFilter;

    @GetMapping("/status")
    public ResponseEntity<UserDTO> checkAuthStatus(@AuthenticationPrincipal OpenAmUserPrincipal user) {
        return ResponseEntity.ok(new UserDTO(
                user.getUser().getUsername(),
                Arrays.stream(user.getRoles())
                        .map(Role::getFullName)
                        .toArray(String[]::new)
        ));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(
            @Value("openam.auth.domain") String domain,
            @AuthenticationPrincipal OpenAmUserPrincipal user
    ) {
        openAmApi.logoutUser(user.getAuthCookie());
        ResponseCookie newCookie = ResponseCookie.from(OpenAmAuthenticationFilter.OPENAM_COOKIE_NAME, "")
                .domain(domain)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .build();
    }

    @Autowired
    public void setOpenAmAuthenticationFilter(OpenAmAuthenticationFilter openAmAuthenticationFilter) {
        this.openAmAuthenticationFilter = openAmAuthenticationFilter;
    }
}
