package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
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
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.services.DragonService;
import org.zir.dragonieze.sort.LocationSort;
import org.zir.dragonieze.sort.specifications.DragonSpecifications;
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
    public ResponseEntity<String> addDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        String json = dragonService.addDragon(header, dragon);
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) throws JsonProcessingException {
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
            @RequestParam(value = "sort", defaultValue = "ID_ASC") LocationSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) boolean canEdit,
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
    public ResponseEntity<String> updateDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        System.out.println("dsfsf");
        String json = dragonService.updateDragon(header, dragon);
        return ResponseEntity.ok(json);
    }


}

