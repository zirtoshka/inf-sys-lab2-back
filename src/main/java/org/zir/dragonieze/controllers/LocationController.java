package org.zir.dragonieze.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Location;
import org.zir.dragonieze.dragon.repo.LocationRepository;
import org.zir.dragonieze.dto.LocationDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.LocationSort;
import org.zir.dragonieze.sort.specifications.LocationSpecifications;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/loc")
public class LocationController extends Controller {
    private final LocationRepository locationRepository;

    public LocationController(BaseService service, LocationRepository locationRepository) {
        super(service);
        this.locationRepository = locationRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addLocation(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Location location
    ) throws JsonProcessingException {
        Location savedLocation = service.saveEntityWithUser(header, location, Location::setUser, locationRepository);
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
        return ResponseEntity.ok("удалилось ура");
    }

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
            @RequestParam(value = "canEdit", required = false) boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "name", required = false) String name
    ) {
        Specification<Location> specification = Specification.where(
                LocationSpecifications.hasId(id)
                        .and(LocationSpecifications.hasX(x))
                        .and(LocationSpecifications.hasY(y))
                        .and(LocationSpecifications.hasZ(z))
                        .and(LocationSpecifications.hasCanEdit(canEdit))
                        .and(LocationSpecifications.hasUserId(userId))
                        .and(LocationSpecifications.hasName(name))
        );
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
        String json = service.convertToJson(new LocationDTO(updateLocation));
        return ResponseEntity.ok(json);
    }


}
