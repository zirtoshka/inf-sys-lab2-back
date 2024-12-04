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
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dto.LocationDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.LocationSort;
import org.zir.dragonieze.sort.specifications.LocationSpecifications;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/loc")
public class LocationController extends Controller {
    private final LocationRepository locationRepository;
    private final LocationSpecifications locationSpecifications;


    public LocationController(BaseService service, LocationRepository locationRepository, SimpMessagingTemplate messagingTemplate, LocationSpecifications locationSpecifications) {
        super(service,messagingTemplate);
        this.locationRepository = locationRepository;
        this.locationSpecifications = locationSpecifications;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        Location savedLocation = service.saveEntityWithUser(header, location, Location::setUser, locationRepository);
        messagingTemplate.convertAndSend("/topic/locations", Map.of(
                "action", "ADD",
                "data",new LocationDTO(savedLocation))
                );
        String json = service.convertToJson(new LocationDTO(savedLocation));
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "Location")
    public ResponseEntity<String> deleteLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                header,
                id,
                Location::getUser,
                locationRepository
        );
        messagingTemplate.convertAndSend("/topic/locations", Map.of(
                "action", "DELETE",
                "id",id)
                );
        return ResponseEntity.ok(
                "'was deleted': " + id
        );    }

    @GetMapping("/get")
    public Page<LocationDTO> getLocations(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") LocationSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "x", required = false) float x,
            @RequestParam(value = "y", required = false) Integer y,
            @RequestParam(value = "z", required = false) Float z,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "name", required = false) String name
    ) {
        Specification<Location> specification = Specification.where(
                locationSpecifications.hasId(id)
                        .and(locationSpecifications.hasX(x))
                        .and(locationSpecifications.hasY(y))
                        .and(locationSpecifications.hasZ(z))
                        .and(locationSpecifications.hasUserId(userId))
                        .and(locationSpecifications.hasName(name))
        );
        specification=canEditSpec(canEdit, specification, locationSpecifications);
        return locationRepository.findAll(specification,
                        PageRequest.of(offset, limit, sort.getSortValue()))
                .map(LocationDTO::new);
    }


    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "Location")
    public ResponseEntity<String> updateLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        Location updateLocation = service.updateEntityWithUser(
                header,
                location,
                location.getId(),
                locationRepository::findById,
                Location::getUser,
                (old, updated) -> {
                    old.setX(updated.getX());
                    old.setY(updated.getY());
                    old.setCanEdit(updated.getCanEdit());
                },
                locationRepository
        );
        messagingTemplate.convertAndSend("/topic/locations", Map.of(
                "action", "UPDATE",
                "data" ,new LocationDTO(updateLocation))
                );
        String json = service.convertToJson(new LocationDTO(updateLocation));
        return ResponseEntity.ok(json);
    }
}
