package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.user.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Component
public class Controller {
    protected final JwtUtil jwtUtil;
    protected final UserRepository userRepository;
    protected final String HEADER_AUTH = "Authorization";

    @Autowired
    public Controller(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    String getUsername(String header, JwtUtil jwtUtil) {
        String jwt = header.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        return username;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    String getJson(Object entity) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(entity);
        return json;
    }
}
