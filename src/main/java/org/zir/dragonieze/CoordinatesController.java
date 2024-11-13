package org.zir.dragonieze;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.Dragon;
import org.zir.dragonieze.dragon.repo.CoordinatesRepository;
import org.zir.dragonieze.dragon.repo.DragonRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/coord")
public class CoordinatesController extends Controller {
    private final CoordinatesRepository coordinatesRepository;

    public CoordinatesController(JwtUtil jwtUtil, UserRepository userRepository, CoordinatesRepository coordinatesRepository) {
        super(jwtUtil, userRepository);
        this.coordinatesRepository = coordinatesRepository;
    }


    @Transactional
    @PostMapping("/addCoordinates")
    public ResponseEntity<String> addCoordinates(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Coordinates coordinates
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        coordinates.setUser(user);
        coordinatesRepository.save(coordinates);
        String json = getJson(new CoordinatesDTO(coordinates));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/getCoordinates")
    public ResponseEntity<String> getCoordinates(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<Coordinates> coordinates = coordinatesRepository.findByUserId(user.getId());
        List<CoordinatesDTO> coordinatesDTOs = coordinates.stream()
                .map(CoordinatesDTO::new)
                .collect(Collectors.toList());
        String json = getJson(coordinatesDTOs);
        System.out.println("it's method getCoordinates");
        return ResponseEntity.ok(json);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllCoordinates")
    public ResponseEntity<String> getALLCoordinates(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        List<Coordinates> coordinates = coordinatesRepository.findAll();
        List<CoordinatesDTO> coordinatesDTOs = coordinates.stream()
                .map(CoordinatesDTO::new)
                .collect(Collectors.toList());
        String json = getJson(coordinatesDTOs);
        System.out.println("it's method getCoordinates");
        return ResponseEntity.ok(json);
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateCoordinates(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable String id,
            @RequestBody Map<String, Object> updateData
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<Coordinates> coordinatesOptional = coordinatesRepository.findByIdAndUserId(Long.parseLong(id), userOptional.get().getId());
        if (!coordinatesOptional.isPresent()) {
            return new ResponseEntity<>("Coordinates not found", HttpStatus.NOT_FOUND);
        }
        Coordinates coordinates = coordinatesOptional.get();
        updateData.forEach((field, value) -> {
            try {
                Field declaredField = Coordinates.class.getDeclaredField(field);
                declaredField.setAccessible(true);
                declaredField.set(coordinates, value);
                declaredField.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("error updatinf field: " + field, e);
            }
        });
        coordinatesRepository.save(coordinates);
        String json = getJson(new CoordinatesDTO(coordinates));
        return ResponseEntity.ok(json);

    }


}
