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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.repo.CoordinatesRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.CoordinatesSort;
import org.zir.dragonieze.sort.specifications.CoordinatesSpecifications;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/coord")
public class CoordinatesController extends Controller {
    private final CoordinatesRepository coordinatesRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public CoordinatesController(CoordinatesRepository coordinatesRepository,
                                 BaseService service, SimpMessagingTemplate messagingTemplate) {
        super(service);
        this.coordinatesRepository = coordinatesRepository;
        this.messagingTemplate = messagingTemplate;
    }


    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addCoordinates(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Coordinates coordinates
    ) throws JsonProcessingException {
        Coordinates savedCoordinates = service.saveEntityWithUser(header, coordinates, Coordinates::setUser, coordinatesRepository);
        String json = service.convertToJson(new CoordinatesDTO(savedCoordinates));

        messagingTemplate.convertAndSend("/topic/updates", new CoordinatesDTO(savedCoordinates)); //todo

        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "Coordinates")
    public ResponseEntity<String> deleteCoordinates(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                header,
                id,
                Coordinates::getUser,
                coordinatesRepository
        );

        messagingTemplate.convertAndSend("/topic/updates", "Deleted: " + id);

        return ResponseEntity.ok("удалилось ура");
    }

    @GetMapping("/get")
    public Page<CoordinatesDTO> getAllCoordinates(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") CoordinatesSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "y", required = false) Float y,
            @RequestParam(value = "canEdit", required = false) boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId

    ) {
        Specification<Coordinates> spec = Specification.where(
                CoordinatesSpecifications.hasId(id)
                        .and(CoordinatesSpecifications.hasX(x))
                        .and(CoordinatesSpecifications.hasY(y))
                        .and(CoordinatesSpecifications.hasCanEdit(canEdit))
                        .and(CoordinatesSpecifications.hasUserId(userId))
        );


        return coordinatesRepository.findAll(spec,
                PageRequest.of(offset, limit, sort.getSortValue())
        ).map(CoordinatesDTO::new);
    }

    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "Coordinates")
    public ResponseEntity<String> updateCoordinates(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Coordinates coordinates
    ) throws JsonProcessingException {
        Coordinates updateCoordinates = service.updateEntityWithUser(
                header,
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
        String json = service.convertToJson(new CoordinatesDTO(updateCoordinates));
        return ResponseEntity.ok(json);
    }


}
