package org.zir.dragonieze;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.repo.DragonRepository;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.dto.LocationDTO;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/loc")
public class LocationController extends Controller {
    private final LocationRepository locationRepository;

    public LocationController(JwtUtil jwtUtil, UserRepository userRepository, LocationRepository locationRepository) {
        super(jwtUtil, userRepository);
        this.locationRepository = locationRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        Location savedLocation = saveEntityWithUser(header, location, Location::setUser, locationRepository);
        String json = getJson(new LocationDTO(savedLocation));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/get")
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

    @Transactional
    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        Location updateLocation = updateEntityWithUser(
                header,
                location,
                location.getId(),
                locationRepository::findById,
                Location::getUser,
                (old, updated) -> {
                    old.setX(updated.getX());
                    old.setY(updated.getY());
                    old.setCanEdit(updated.getCanEdit());
                },
                locationRepository
        );
        String json = getJson(new LocationDTO(updateLocation));
        return ResponseEntity.ok(json);
    }


}
