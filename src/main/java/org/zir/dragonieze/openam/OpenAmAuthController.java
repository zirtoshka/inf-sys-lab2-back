package org.zir.dragonieze.openam;


import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dto.UserDTO;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.user.Role;

import java.util.Arrays;

@RestController
@RequestMapping("/dragon/am")
public class OpenAuthController {
    @GetMapping("/status")
    public ResponseEntity<UserDTO> checkAuthStatus(@AuthenticationPrincipal OpenAmUserPrincipal user) {
        return ResponseEntity.ok(new UserDTO(
                user.getUser().getUsername(),
                Arrays.stream(user.getRoles())
                        .map(Role::getFullName)
                        .toArray(String[]::new)
        ));
    }
}
