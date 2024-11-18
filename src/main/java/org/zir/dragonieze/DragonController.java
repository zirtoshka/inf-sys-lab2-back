package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.*;
import org.zir.dragonieze.dragon.repo.*;
import org.zir.dragonieze.dto.DragonDTO;
import org.zir.dragonieze.user.UserRepository;
import org.zir.dragonieze.user.User;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/dragon/user/dragon")
public class DragonController extends Controller {

    private final DragonRepository dragonRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final DragonCaveRepository caveRepository;
    private final PersonRepository personRepository;
    private final DragonHeadRepository headRepository;

    public DragonController(JwtUtil jwtUtil, UserRepository userRepository, DragonRepository dragonRepository, CoordinatesRepository coordinatesRepository, DragonCaveRepository caveRepository, PersonRepository personRepository, DragonHeadRepository headRepository) {
        super(jwtUtil, userRepository);
        this.dragonRepository = dragonRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.caveRepository = caveRepository;
        this.personRepository = personRepository;
        this.headRepository = headRepository;
    }


    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method sayHello");
        return new ResponseEntity<>("{\"message\": \"Hello from secured endpoint\"}", httpHeaders, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        Coordinates coordinates = validateAndGetEntity(dragon.getCoordinates().getId(), coordinatesRepository, "Coordinates");
        dragon.setCoordinates(coordinates);

        DragonCave cave = validateAndGetEntity(dragon.getCave().getId(), caveRepository, "Cave");
        dragon.setCave(cave);

        if (dragon.getKiller() != null) {
            Person person = validateAndGetEntity(dragon.getKiller().getId(), personRepository, "Killer");
            dragon.setKiller(person);
        } else {
            dragon.setKiller(null);
        }

        if (dragon.getHeads() == null || dragon.getHeads().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one dragon head is required");
        }

        List<DragonHead> validatedHeads = new ArrayList<>();
        for (DragonHead head : dragon.getHeads()) {
            if (head.getId() > 0) {
                DragonHead existingHead = headRepository.findById(head.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dragon head with ID " + head.getId() + " not found"));
                validatedHeads.add(existingHead);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dragon head is not ok");
            }
        }

        dragon.setHeads(validatedHeads);
        dragon.setCreationDate(LocalDate.now());


        Dragon savedDragon = saveEntityWithUser(header, dragon, Dragon::setUser, dragonRepository);
        String json = getJson(new DragonDTO(savedDragon));
        return ResponseEntity.ok(json);
    }


    @GetMapping("/getDragons")
    public ResponseEntity<String> getDragons(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        System.out.println(jwtUtil + "sdsd");
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<Dragon> dragons = dragonRepository.findByUserId(user.getId());
        List<DragonDTO> dragonDTOs = dragons.stream()
                .map(dragon -> new DragonDTO(dragon))
                .collect(Collectors.toList());
        String json = getJson(dragonDTOs);
        System.out.println("it's method getDragons");
        return ResponseEntity.ok(json);
    }


}

