package org.zir.dragonieze.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.Person;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dragon.repo.PersonRepository;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;
    private final BaseService baseService;

    @Autowired
    public PersonService(PersonRepository personRepository, LocationRepository locationRepository, BaseService baseService) {
        this.personRepository = personRepository;
        this.locationRepository = locationRepository;
        this.baseService = baseService;
    }

    public Person setLocationForPerson(Person person) {
        if (person.getLocation() != null) {
            Location location = baseService.validateAndGetEntity(
                    person.getLocation().getId(), locationRepository, "Location");
            person.setLocation(location);
        } else {
            person.setLocation(null);
        }
        return person;
    }


    @Transactional(propagation = Propagation.MANDATORY)
    public String ensureUniquePassportId(String passportId) throws CannotCreateTransactionException {
        String uniquePassportId = passportId;

        while (personRepository.existsByPassportID(uniquePassportId)) {
            uniquePassportId = UUID.randomUUID().toString();
        }

        return uniquePassportId;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Location validateAndRetrieveLocation(Location location) {
        if (location == null) {
            return null;
        }
        return baseService.validateAndGetEntity(location.getId(), locationRepository, "Location");
    }


    @Retryable(
            value = {org.springframework.dao.ConcurrencyFailureException.class, org.springframework.transaction.TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, maxDelay = 5000, multiplier = 2.0, random = true)
    )
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public List<Person> savePersonDB(MultipartFile file, OpenAmUserPrincipal user) throws Exception {
        List<Person> persons = parsePersonsFromFile(file);

        persons = persons.stream()
                .map(this::preparePerson)
                .toList();
        List<Person> savedPersons = savePersons(persons, user);
        return savedPersons;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Person> parsePersonsFromFile(MultipartFile file) throws IOException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        return yamlMapper.readValue(file.getInputStream(), new TypeReference<List<Person>>() {
        });
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Person preparePerson(Person person) throws CannotCreateTransactionException {
        try {
            person = setLocationForPerson(person);
            String uniquePassportId = ensureUniquePassportId(person.getPassportID());
            person.setPassportID(uniquePassportId);
            return person;
        } catch (CannotCreateTransactionException e) {
            System.out.println("LOLO");
            throw e;
        }

    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Person> savePersons(List<Person> persons, OpenAmUserPrincipal user) throws Exception {
        List<Person> savedPersons = new ArrayList<>();
        for (Person person : persons) {
            Person savedPerson = baseService.saveEntityWithUser(user, person, Person::setUser, personRepository);
            savedPersons.add(savedPerson);
        }
        return savedPersons;
    }


}


