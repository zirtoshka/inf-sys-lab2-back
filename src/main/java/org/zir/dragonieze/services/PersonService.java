package org.zir.dragonieze.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.Person;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dragon.repo.PersonRepository;

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
    public String ensureUniquePassportId(String passportId) {
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

}


