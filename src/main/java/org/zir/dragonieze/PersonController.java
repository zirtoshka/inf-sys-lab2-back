package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.Person;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dragon.repo.PersonRepository;
import org.zir.dragonieze.dto.PersonDTO;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.user.User;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/person")
public class PersonController extends Controller {
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;

    public PersonController(BaseService service, PersonRepository personRepository, LocationRepository locationRepository) {
        super(service);
        this.personRepository = personRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addPerson(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Person person
    ) throws JsonProcessingException {
        if (person.getLocation() != null) {
            Location location = service.validateAndGetEntity(person.getLocation().getId(), locationRepository, "Location");
            person.setLocation(location);
        } else {
            person.setLocation(null);
        }
        Person savedPerson = service.saveEntityWithUser(header, person, Person::setUser, personRepository);
        String json = service.convertToJson(new PersonDTO(savedPerson));
        return ResponseEntity.ok(json);
    }

//    @GetMapping("/get")
//    public ResponseEntity<String> getPersons(
//            @RequestHeader(HEADER_AUTH) String header
//    ) throws JsonProcessingException {
//        String username = getUsername(header, jwtUtil);
//        Optional<User> userOptional = userRepository.findByUsername(username);
//
//        if (!userOptional.isPresent()) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//        User user = userOptional.get();
//        List<Person> personList = personRepository.findByUserId(user.getId());
//        List<PersonDTO> personDTOS = personList.stream()
//                .map(PersonDTO::new)
//                .toList();
//        String json = convertToJson(personDTOS);
//        return ResponseEntity.ok(json);
//    }


    @Transactional
    @PostMapping("/update")
    public ResponseEntity<String> updatePerson(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Person person
    ) throws JsonProcessingException {
        Person updatePerson = service.updateEntityWithUser(
                header,
                person,
                person.getId(),
                personRepository::findById,
                Person::getUser,
                (old, updated) -> {
                    old.setName(updated.getName());
                    old.setEyeColor(updated.getEyeColor());
                    old.setHairColor(updated.getHairColor());
                    old.setLocation(validateAndRetrieveLocation(updated.getLocation()));
                    old.setHeight(updated.getHeight());
                    old.setPassportID(updated.getPassportID());
                    old.setNationality(updated.getNationality());
                    old.setCanEdit(updated.getCanEdit());
                },
                personRepository
        );
        String json = service.convertToJson(new PersonDTO(updatePerson));
        return ResponseEntity.ok(json);
    }

    private Location validateAndRetrieveLocation(Location location) {
        if (location == null) {
            return null;
        }
        return service.validateAndGetEntity(location.getId(), locationRepository, "Location");
    }

}
