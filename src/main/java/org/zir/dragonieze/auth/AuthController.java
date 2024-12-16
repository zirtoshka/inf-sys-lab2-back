package org.zir.dragonieze.auth;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;


@RestController
@RequestMapping("/dragon/auth")
@AllArgsConstructor
public class AuthController {
    @MessageMapping("/update") // Клиент отправляет сообщение на "/app/update"
    @SendTo("/topic/updates") // Сообщение транслируется всем, кто подписан на "/topic/updates"
    public String sendNotification(String message) {
        return message; // Например, строка JSON-данных об изменении
    }

}
