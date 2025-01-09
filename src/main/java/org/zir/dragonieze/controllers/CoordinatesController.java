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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.GeneralEntity;
import org.zir.dragonieze.dragon.repo.CoordinatesRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.CoordinatesSort;
import org.zir.dragonieze.sort.specifications.CoordinatesSpecifications;

import java.util.Map;


@RestController
@RequestMapping("/dragon/coord")
public class CoordinatesController extends Controller {
    private final CoordinatesRepository coordinatesRepository;
    private final CoordinatesSpecifications coordinatesSpecifications;



    public CoordinatesController(CoordinatesRepository coordinatesRepository,
                                 BaseService service, SimpMessagingTemplate messagingTemplate, CoordinatesSpecifications coordinatesSpecifications) {
        super(service, messagingTemplate);
        this.coordinatesRepository = coordinatesRepository;
        this.coordinatesSpecifications = coordinatesSpecifications;
    }


    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addCoordinates(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody Coordinates coordinates
    ) throws Exception {
        Coordinates savedCoordinates = service.saveEntityWithUser(user, coordinates, Coordinates::setUser, coordinatesRepository);
        String json = service.convertToJson(new CoordinatesDTO(savedCoordinates));
        messagingTemplate.convertAndSend("/topic/coordinates", Map.of(
                "action", "ADD",
                "data", new CoordinatesDTO(savedCoordinates))
        );
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "Coordinates")
    public ResponseEntity<String> deleteCoordinates(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                user,
                id,
                Coordinates::getUser,
                coordinatesRepository
        );

        messagingTemplate.convertAndSend("/topic/coordinates", Map.of(
                "action", "DELETE",
                "id", id
        ));
        return ResponseEntity.ok(
                "'was deleted': " + id
        );
    }

    @GetMapping("/get")
    public Page<CoordinatesDTO> getAllCoordinates(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") CoordinatesSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "y", required = false) Float y,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId

    ) {
        Specification<Coordinates> spec = Specification.where(
                coordinatesSpecifications.hasId(id)
                        .and(coordinatesSpecifications.hasX(x))
                        .and(coordinatesSpecifications.hasY(y))
                        .and(coordinatesSpecifications.hasUserId(userId))
        );

        spec = canEditSpec(canEdit, spec, coordinatesSpecifications);

        return coordinatesRepository.findAll(spec,
                PageRequest.of(offset, limit, sort.getSortValue())
        ).map(CoordinatesDTO::new);
    }

    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "Coordinates")
    public ResponseEntity<String> updateCoordinates(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody Coordinates coordinates
    ) throws JsonProcessingException {
        Coordinates updateCoordinates = service.updateEntityWithUser(
                user,
                coordinates,
                coordinates.getId(),
                coordinatesRepository::findById,
                Coordinates::getUser,
                (old, updated) -> {
                    old.setX(updated.getX());
                    old.setY(updated.getY());
                    old.setCanEdit(updated.getCanEdit());
                },
                coordinatesRepository
        );
        messagingTemplate.convertAndSend("/topic/coordinates", Map.of(
                "action", "UPDATE",
                "data", new CoordinatesDTO(updateCoordinates))
        );
        String json = service.convertToJson(new CoordinatesDTO(updateCoordinates));
        return ResponseEntity.ok(json);
    }


}
