package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.DragonCave;
import org.zir.dragonieze.dragon.repo.CoordinatesRepository;
import org.zir.dragonieze.dragon.repo.DragonCaveRepository;
import org.zir.dragonieze.dragon.repo.DragonRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.dto.DragonCaveDTO;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dragon/user/cave")
@CrossOrigin(origins = "*")

public class DragonCaveController extends Controller {
    private final DragonCaveRepository caveRepository;

    public DragonCaveController(JwtUtil jwtUtil, UserRepository userRepository, DragonCaveRepository caveRepository) {
        super(jwtUtil, userRepository);
        this.caveRepository = caveRepository;
    }

    @Transactional
    @PostMapping("/addCave")
    public ResponseEntity<String> addCave(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonCave cave
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        cave.setUser(user);
        caveRepository.save(cave);
        String json = getJson(new DragonCaveDTO(cave));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/getCaves")
    public ResponseEntity<String> getCaves(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<DragonCave> caves = caveRepository.findByUserId(user.getId());
        List<DragonCaveDTO> caveDTOS = caves.stream()
                .map(DragonCaveDTO::new)
                .collect(Collectors.toList());
        String json = getJson(caveDTOS);
        System.out.println("it's method getCaves");
        return ResponseEntity.ok(json);
    }
}
