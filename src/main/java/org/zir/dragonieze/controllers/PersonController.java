package org.zir.dragonieze.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zir.dragonieze.dragon.*;
import org.zir.dragonieze.dragon.repo.PersonRepository;
import org.zir.dragonieze.dto.PersonDTO;
import org.zir.dragonieze.imphist.ImportHistoryService;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.services.PersonService;
import org.zir.dragonieze.sort.PersonSort;
import org.zir.dragonieze.sort.specifications.PersonSpecifications;
import org.springframework.retry.annotation.Retryable;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/dragon/person")
public class PersonController extends Controller {
    private final PersonRepository personRepository;
    private final PersonSpecifications personSpecifications;
    private final PersonService personService;
    private final ImportHistoryService importHistoryService;

    public PersonController(BaseService service, PersonRepository personRepository, SimpMessagingTemplate messagingTemplate, PersonSpecifications personSpecifications, PersonService personService, ImportHistoryService importHistoryService) {
        super(service, messagingTemplate);
        this.personRepository = personRepository;
        this.personSpecifications = personSpecifications;
        this.personService = personService;
        this.importHistoryService = importHistoryService;
    }

    @Retryable(
            value = {SQLException.class, org.springframework.dao.ConcurrencyFailureException.class, org.springframework.transaction.TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, maxDelay = 5000, multiplier = 2.0, random = true)
    )
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping("/add")
    public ResponseEntity<String> addPerson(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody Person person
    ) throws Exception {
        person = personService.setLocationForPerson(person);
        String uniquePassportId = personService.ensureUniquePassportId(person.getPassportID());
        person.setPassportID(uniquePassportId);

        Person savedPerson = service.saveEntityWithUser(user, person, Person::setUser, personRepository);
        messagingTemplate.convertAndSend("/topic/persons", Map.of(
                "action", "ADD",
                "data", new PersonDTO(savedPerson))
        );
        String json = service.convertToJson(new PersonDTO(savedPerson));
        return ResponseEntity.ok(json);

    }


    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importPersons(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @RequestParam("file") MultipartFile file
    ) {
        return importHistoryService.importPerson(user, file);

//        try {
//
//
//            sendNotificationAfterCommit("/topic/persons", Map.of(
//                    "action", "IMPORT",
//                    "data", savedPersons.stream().map(PersonDTO::new).toList()
//            ));
//            return ResponseEntity.ok(Map.of(
//                    "message", "Successfully imported persons",
//                    "importedCount", savedPersons.size()));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "message", "Failed to process file: " + e.getMessage(),
//                    "importedCount", 0));
//        } catch (CannotCreateTransactionException | DataAccessException ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("ss", ""));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to process file: " + e.getMessage()));
//        }
    }

    @Retryable(
            value = {SQLException.class, org.springframework.dao.ConcurrencyFailureException.class, org.springframework.transaction.TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, maxDelay = 5000, multiplier = 2.0, random = true)
    )
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "Person")
    public ResponseEntity<String> deletePerson(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                user,
                id,
                Person::getUser,
                personRepository
        );
        messagingTemplate.convertAndSend("/topic/persons", Map.of(
                "action", "DELETE",
                "id", id
        ));
        return ResponseEntity.ok(
                "'was deleted': " + id
        );
    }

    @Transactional
    @GetMapping("/get")
    public Page<PersonDTO> getPersons(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") PersonSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "hairColor", required = false) Color hair,
            @RequestParam(value = "eyeColor", required = false) Color eye,
            @RequestParam(value = "locationId", required = false) Long locationId,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "passportID", required = false) String passportID,
            @RequestParam(value = "nationality", required = false) Country nationality
    ) {
        Specification<Person> specification = Specification.where(
                personSpecifications.hasId(id)
                        .and(personSpecifications.hasName(name))
                        .and(personSpecifications.hasUserId(userId))
                        .and(personSpecifications.hasHair(hair))
                        .and(personSpecifications.hasLocation(locationId))
                        .and(personSpecifications.hasHeight(height))
                        .and(personSpecifications.hasPassportID(passportID))
                        .and(personSpecifications.hasNationality(nationality))
                        .and(personSpecifications.hasEyes(eye))
        );
        specification = canEditSpec(canEdit, specification, personSpecifications);
        return personRepository.findAll(specification,
                        PageRequest.of(offset, limit, sort.getSortValue()))
                .map(PersonDTO::new);
    }

    @Retryable(
            value = {SQLException.class, org.springframework.dao.ConcurrencyFailureException.class, org.springframework.transaction.TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, maxDelay = 5000, multiplier = 2.0, random = true)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "Person")
    public ResponseEntity<String> updatePerson(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody Person person
    ) throws JsonProcessingException {
        String uniquePassportId = personService.ensureUniquePassportId(person.getPassportID());
        person.setPassportID(uniquePassportId);

        Person updatePerson = service.updateEntityWithUser(
                user,
                person,
                person.getId(),
                personRepository::findById,
                Person::getUser,
                (old, updated) -> {
                    old.setName(updated.getName());
                    old.setEyeColor(updated.getEyeColor());
                    old.setHairColor(updated.getHairColor());
                    old.setLocation(personService.validateAndRetrieveLocation(updated.getLocation()));
                    old.setHeight(updated.getHeight());
                    old.setPassportID(updated.getPassportID());
                    old.setNationality(updated.getNationality());
                    old.setCanEdit(updated.getCanEdit());
                },
                personRepository
        );
        messagingTemplate.convertAndSend("/topic/persons", Map.of(
                "action", "UPDATE",
                "data", new PersonDTO(updatePerson))
        );

        String json = service.convertToJson(new PersonDTO(updatePerson));
        return ResponseEntity.ok(json);
    }


}
