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

import org.zir.dragonieze.dragon.DragonCave;
import org.zir.dragonieze.dragon.repo.DragonCaveRepository;
import org.zir.dragonieze.dto.DragonCaveDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.CaveSort;
import org.zir.dragonieze.sort.specifications.CaveSpecifications;
import org.zir.dragonieze.sort.specifications.DragonSpecifications;

import java.util.Map;


@RestController
@RequestMapping("/dragon/cave")
public class DragonCaveController extends Controller {
    private final DragonCaveRepository caveRepository;
    private final CaveSpecifications caveSpecifications;

    public DragonCaveController(BaseService baseService, DragonCaveRepository caveRepository, SimpMessagingTemplate messagingTemplate, DragonSpecifications dragonSpecifications, CaveSpecifications caveSpecifications) {
        super(baseService, messagingTemplate);
        this.caveRepository = caveRepository;
        this.caveSpecifications = caveSpecifications;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addCave(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody DragonCave cave
    ) throws JsonProcessingException {
        DragonCave savedCave = service.saveEntityWithUser(user, cave, DragonCave::setUser, caveRepository);
        messagingTemplate.convertAndSend("/topic/caves", Map.of(
                "action", "ADD",
                "data", new DragonCaveDTO(savedCave))
        );
        String json = service.convertToJson(new DragonCaveDTO(savedCave));
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "DragonCave")
    public ResponseEntity<String> deleteCave(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                user,
                id,
                DragonCave::getUser,
                caveRepository
        );
        messagingTemplate.convertAndSend("/topic/caves", Map.of(
                "action", "DELETE",
                "id", id
        ));

        return ResponseEntity.ok("'was deleted': " + id);
    }

    @GetMapping("/get")
    public Page<DragonCaveDTO> getCaves(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") CaveSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit, //todo
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "numberOfTreasures", required = false) Integer treasure
    ) {
        Specification<DragonCave> specification = Specification.where(
                caveSpecifications.hasId(id)
                        .and(caveSpecifications.hasUserId(userId))
                        .and(caveSpecifications.hasTreasures(treasure))
        );
        specification = canEditSpec(canEdit, specification, caveSpecifications);

        return caveRepository.findAll(
                specification,
                PageRequest.of(offset, limit, sort.getSortValue())
        ).map(DragonCaveDTO::new);

    }


    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "DragonCave")
    public ResponseEntity<String> updateCave(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody DragonCave cave
    ) throws JsonProcessingException {
        System.out.println(cave.getCanEdit());
        DragonCave updatedCave = service.updateEntityWithUser(
                user,
                cave,
                cave.getId(),
                caveRepository::findById,
                DragonCave::getUser,
                (old, updated) -> {
                    old.setNumberOfTreasures(updated.getNumberOfTreasures());
                    old.setCanEdit(updated.getCanEdit());
                },
                caveRepository
        );
        messagingTemplate.convertAndSend("/topic/caves", Map.of(
                "action", "UPDATE",
                "data", new DragonCaveDTO(updatedCave))
        );
        String json = service.convertToJson(new DragonCaveDTO(updatedCave));
        return ResponseEntity.ok(json);
    }
}
