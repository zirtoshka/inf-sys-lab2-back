package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.Dragon;
import org.zir.dragonieze.dragon.repo.CoordinatesRepository;
import org.zir.dragonieze.dragon.repo.DragonRepository;
import org.zir.dragonieze.user.UserRepository;
import org.zir.dragonieze.user.User;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dragon/user/dr")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DragonController extends Controller {
    private final DragonRepository dragonRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method sayHello");
        return new ResponseEntity<>("{\"message\": \"Hello from secured endpoint\"}", httpHeaders, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/addDragon")
    public ResponseEntity<String> addDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        dragon.setUser(user); // Связываем дракона с пользователем

        Dragon savedDragon = dragonRepository.save(dragon);
        String json = getJson(savedDragon);
        return ResponseEntity.ok(json);
    }


    @GetMapping("/getDragons")
    public ResponseEntity<String> getDragons(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<Dragon> dragons = dragonRepository.findByUserId(user.getId());
        String json = getJson(dragons);
        System.out.println("it's method getDragons");
        return ResponseEntity.ok(json);
    }


}

