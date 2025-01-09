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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.*;
import org.zir.dragonieze.dto.DragonDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.services.DragonService;
import org.zir.dragonieze.sort.DragonSort;
import org.zir.dragonieze.sort.specifications.DragonSpecifications;


import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/dragon/dragon")
public class DragonController extends Controller {

    private final DragonService dragonService;
    private final  DragonSpecifications dragonSpecifications;


    public DragonController(BaseService baseService, DragonService dragonService, SimpMessagingTemplate messagingTemplate, DragonSpecifications dragonSpecifications) {
        super(baseService,messagingTemplate);
        this.dragonService = dragonService;
        this.dragonSpecifications = dragonSpecifications;
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
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody Dragon dragon
    ) throws Exception {
        DragonDTO dragonDTO = dragonService.addDragon(user, dragon);
        messagingTemplate.convertAndSend("/topic/dragons", Map.of(
                "action", "ADD",
                "data", dragonDTO
        ));
        String json = service.convertToJson(dragonDTO);
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action="DELETE", entity = "Dragon")
    public ResponseEntity<String> deleteDragon(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                user,
                id,
                Dragon::getUser,
                dragonService.getDragonRepository()
        );
        messagingTemplate.convertAndSend("/topic/dragons", Map.of(
                "action", "DELETE",
                "id", id
        ));
        return ResponseEntity.ok(
                "'was deleted': " + id
        );    }

    @GetMapping("/get")
    public Page<DragonDTO> getDragons(
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
            @RequestParam(value = "wingspan", required = false) Long wingspan,
            @RequestParam(value = "character", required = false) DragonCharacter character,
            @RequestParam(value = "headCount", required = false) Integer headCount,

            @RequestParam(value = "minX", required = false) Double minX,
            @RequestParam(value = "maxX", required = false) Double maxX,
            @RequestParam(value = "minY", required = false) Float minY,
            @RequestParam(value = "maxY", required = false) Float maxY
    ){
        Specification<Dragon> specification = Specification.where(
                dragonSpecifications.hasId(id)
                        .and(dragonSpecifications.hasUserId(userId))
                        .and(dragonSpecifications.hasName(name))
                        .and(dragonSpecifications.hasColor(color))
                        .and(dragonSpecifications.hasCoordinates(coordId))
                        .and(dragonSpecifications.hasCreationDate(creationDate))
                        .and(dragonSpecifications.hasKiller(killerId))
                        .and(dragonSpecifications.hasAge(age))
                        .and(dragonSpecifications.hasWingspan(wingspan))
                        .and(dragonSpecifications.hasCave(cavedId))
                        .and(dragonSpecifications.hasCharacter(character))
                        .and(dragonSpecifications.hasHeads(headCount))
        );

        if (minX != null && maxX != null && minY != null && maxY != null) {
            specification = specification.and(dragonSpecifications.coordinatesInRectangle(minX, maxX, minY, maxY));
        }


        specification = canEditSpec(canEdit, specification, dragonSpecifications);
        return dragonService.getDragonRepository().findAll(
                specification,
                PageRequest.of(offset,limit,sort.getSortValue())
        ).map(DragonDTO::new);
    }



    @Transactional
    @PostMapping("/update")
    @Auditable(action="UPDATE", entity = "Dragon")
    public ResponseEntity<String> updateDragon(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        DragonDTO updateDragon = dragonService.updateDragon(user, dragon);
        messagingTemplate.convertAndSend("/topic/dragons", Map.of(
                "action", "UPDATE",
                "data", updateDragon)
                );
        String json = service.convertToJson(updateDragon);
        return ResponseEntity.ok(json);
    }


}

