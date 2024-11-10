package org.zir.dragonieze;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.dto.LocationDTO;
import org.zir.dragonieze.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/loc")
public class LocationController extends Controller {
    private final LocationRepository locationRepository;

    @Transactional
    @PostMapping("/addLocation")
    public ResponseEntity<String> addLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        location.setUser(user);
        locationRepository.save(location);
        String json = getJson(new LocationDTO(location));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/getLocations")
    public ResponseEntity<String> getLocations(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<Location> locations = locationRepository.findByUserId(user.getId());
        List<LocationDTO> locationDTOS = locations.stream()
                .map(LocationDTO::new)
                .toList();
        String json = getJson(locationDTOS);
        return ResponseEntity.ok(json);
    }

}
