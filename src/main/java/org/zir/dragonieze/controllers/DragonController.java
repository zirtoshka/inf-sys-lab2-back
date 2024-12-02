package org.zir.dragonieze.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.*;
import org.zir.dragonieze.dto.DragonDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.services.DragonService;
import org.zir.dragonieze.sort.DragonSort;
import org.zir.dragonieze.sort.LocationSort;
import org.zir.dragonieze.sort.specifications.DragonSpecifications;


import java.time.LocalDate;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/dragon/user/dragon")
public class DragonController extends Controller {

    private final DragonService dragonService;

    public DragonController(BaseService baseService, DragonService dragonService) {
        super(baseService);
        this.dragonService = dragonService;
    }


    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method sayHello");
        return new ResponseEntity<>("{\"message\": \"Hello from secured endpoint\"}", httpHeaders, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/add")
    @Auditable(action="DELETE", entity = "Dragon")
    public ResponseEntity<String> addDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        String json = dragonService.addDragon(header, dragon);
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action="DELETE", entity = "Dragon")
    public ResponseEntity<String> deleteDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                header,
                id,
                Dragon::getUser,
                dragonService.getDragonRepository()
        );
        return ResponseEntity.ok("удалилось ура");
    }

    @GetMapping("/get")
    public Page<DragonDTO> getDragons(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") DragonSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "color", required = false) Color color,
            @RequestParam(value = "coordinatesId", required = false) Long coordId,
            @RequestParam(value = "creationDate", required = false) LocalDate creationDate,
            @RequestParam(value = "caveId", required = false) Long cavedId,
            @RequestParam(value = "killerId", required = false) Long killerId,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "wingspan", required = false) long wingspan,
            @RequestParam(value = "character", required = false) DragonCharacter character,
            @RequestParam(value = "headCount", required = false) int headCount
    ){
        Specification<Dragon> specification = Specification.where(
                DragonSpecifications.hasId(id)
                        .and(DragonSpecifications.hasUserId(userId))
                        .and(DragonSpecifications.hasName(name))
                        .and(DragonSpecifications.hasColor(color))
                        .and(DragonSpecifications.hasCoordinates(coordId))
                        .and(DragonSpecifications.hasCreationDate(creationDate))
                        .and(DragonSpecifications.hasKiller(killerId))
                        .and(DragonSpecifications.hasAge(age))
                        .and(DragonSpecifications.hasCanEdit(canEdit))
                        .and(DragonSpecifications.hasWingspan(wingspan))
                        .and(DragonSpecifications.hasCave(cavedId))
                        .and(DragonSpecifications.hasCharacter(character))
                        .and(DragonSpecifications.hasHeads(headCount))
        );
        return dragonService.getDragonRepository().findAll(
                specification,
                PageRequest.of(offset,limit,sort.getSortValue())
        ).map(DragonDTO::new);
    }


    @Transactional
    @PostMapping("/update")
    @Auditable(action="UPDATE", entity = "Dragon")
    public ResponseEntity<String> updateDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        String json = dragonService.updateDragon(header, dragon);
        return ResponseEntity.ok(json);
    }


}

