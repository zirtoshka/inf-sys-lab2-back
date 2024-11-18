package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.Person;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dragon.repo.PersonRepository;
import org.zir.dragonieze.dto.PersonDTO;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/person")
public class PersonController extends Controller {
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;

    public PersonController(JwtUtil jwtUtil, UserRepository userRepository, PersonRepository personRepository, LocationRepository locationRepository) {
        super(jwtUtil, userRepository);
        this.personRepository = personRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addPerson(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Person person
    ) throws JsonProcessingException {
        if (person.getLocation() != null && person.getLocation().getId() > 0) {
            Optional<Location> locationOptional = locationRepository.findById(person.getLocation().getId());
            if (locationOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
            }
            person.setLocation(locationOptional.get());
        } else {
            person.setLocation(null);
        }
        Person savedPerson = saveEntityWithUser(header, person, Person::setUser, personRepository);
        String json = getJson(new PersonDTO(savedPerson));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPersons(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<Person> personList = personRepository.findByUserId(user.getId());
        List<PersonDTO> personDTOS = personList.stream()
                .map(PersonDTO::new)
                .toList();
        String json = getJson(personDTOS);
        return ResponseEntity.ok(json);
    }


}
