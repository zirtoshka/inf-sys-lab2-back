package org.zir.dragonieze.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.dragon.*;
import org.zir.dragonieze.dragon.repo.*;
import org.zir.dragonieze.dto.DragonDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DragonService {
    @Getter
    private final DragonRepository dragonRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final DragonCaveRepository caveRepository;
    private final PersonRepository personRepository;
    private final DragonHeadRepository headRepository;
    private final BaseService baseService;


    @Autowired
    public DragonService(DragonRepository dragonRepository, CoordinatesRepository coordinatesRepository, DragonCaveRepository caveRepository, PersonRepository personRepository, DragonHeadRepository headRepository, BaseService baseService) {

        this.dragonRepository = dragonRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.caveRepository = caveRepository;
        this.personRepository = personRepository;
        this.headRepository = headRepository;
        this.baseService = baseService;
    }


    public Person validateAndRetrieveKiller(Person person) {
        if (person == null) {
            return null;
        }
        return baseService.validateAndGetEntity(person.getId(), personRepository, "Killer");
    }

    public Coordinates validateAndRetrieveCoordinates(Coordinates coordinates) {
        return baseService.validateAndGetEntity(
                coordinates != null ? coordinates.getId() : null,
                coordinatesRepository,
                "Coordinates"
        );
    }

    public DragonCave validateAndRetrieveCave(DragonCave cave) {
        return baseService.validateAndGetEntity(
                cave != null ? cave.getId() : null,
                caveRepository,
                "DragonCave"
        );
    }

    public List<DragonHead> validateAndRetrieveHeads(List<DragonHead> heads, JpaRepository<DragonHead, Long> repository) {
        if (heads == null || heads.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dragon must have at least one head.");
        }

        List<DragonHead> validatedHeads = new ArrayList<>();

        for (DragonHead head : heads) {
            if (head.getId() > 0) {
                DragonHead existingHead = repository.findById(head.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dragon head with ID " + head.getId() + " not found"));
                validatedHeads.add(existingHead);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid dragon head: ID must be provided for all heads.");
            }
        }

        return validatedHeads;
    }

    public String addDragon(String header, Dragon dragon) throws JsonProcessingException {
        Coordinates coordinates = baseService.validateAndGetEntity(dragon.getCoordinates().getId(), coordinatesRepository, "Coordinates");
        dragon.setCoordinates(coordinates);

        DragonCave cave = baseService.validateAndGetEntity(dragon.getCave().getId(), caveRepository, "Cave");
        dragon.setCave(cave);

        if (dragon.getKiller() != null) {
            Person person = baseService.validateAndGetEntity(dragon.getKiller().getId(), personRepository, "Killer");
            dragon.setKiller(person);
        } else {
            dragon.setKiller(null);
        }

        if (dragon.getHeads() == null || dragon.getHeads().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one dragon head is required");
        }

        List<DragonHead> validatedHeads = validateAndRetrieveHeads(dragon.getHeads(), headRepository);
        dragon.setHeads(validatedHeads);

        dragon.setCreationDate(LocalDate.now());

        Dragon savedDragon = baseService.saveEntityWithUser(header, dragon, Dragon::setUser, dragonRepository);
        String json = baseService.convertToJson(new DragonDTO(savedDragon));
        return json;
    }

    public String updateDragon(String header, Dragon dragon) throws JsonProcessingException {
        Dragon updateDragon = baseService.updateEntityWithUser(
                header,
                dragon,
                dragon.getId(),
                dragonRepository::findById,
                Dragon::getUser,
                (old, updated) -> {
                    old.setName(updated.getName());
                    old.setCoordinates(validateAndRetrieveCoordinates(updated.getCoordinates()));
                    old.setCreationDate(updated.getCreationDate());
                    old.setCave(validateAndRetrieveCave(updated.getCave()));
                    old.setKiller(validateAndRetrieveKiller(updated.getKiller()));
                    old.setAge(updated.getAge());
                    old.setWingspan(updated.getWingspan());
                    old.setColor(updated.getColor());
                    old.setCharacter(updated.getCharacter());
                    old.setCanEdit(updated.getCanEdit());
                    List<DragonHead> validatedHeads = validateAndRetrieveHeads(updated.getHeads(), headRepository);
                    old.setHeads(validatedHeads);
                },
                dragonRepository
        );


        String json = baseService.convertToJson(new DragonDTO(updateDragon));
        return json;
    }
}
