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

import org.zir.dragonieze.dragon.DragonCave;
import org.zir.dragonieze.dragon.repo.DragonCaveRepository;
import org.zir.dragonieze.dto.DragonCaveDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.LocationSort;
import org.zir.dragonieze.sort.specifications.CaveSpecifications;


@RestController
@RequestMapping("/dragon/user/cave")
@CrossOrigin(origins = "*")

public class DragonCaveController extends Controller {
    private final DragonCaveRepository caveRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public DragonCaveController(BaseService baseService, DragonCaveRepository caveRepository, SimpMessagingTemplate messagingTemplate) {
        super(baseService);
        this.caveRepository = caveRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addCave(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonCave cave
    ) throws JsonProcessingException {
        DragonCave savedCave = service.saveEntityWithUser(header, cave, DragonCave::setUser, caveRepository);
        messagingTemplate.convertAndSend("/topic/caves", new DragonCaveDTO(savedCave)); // Отправляем сообщение всем клиентам
        String json = service.convertToJson(new DragonCaveDTO(savedCave));
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "DragonCave")
    public ResponseEntity<String> deleteCave(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                header,
                id,
                DragonCave::getUser,
                caveRepository
        );
        messagingTemplate.convertAndSend("/topic/caves", id); // Отправляем id удалённой пещеры

        return ResponseEntity.ok("удалилось ура");
    }

    @GetMapping("/get")
    public Page<DragonCaveDTO> getCaves(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") LocationSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "numberOfTreasures", required = false) Integer treasure
    ) {
        Specification<DragonCave> specification = Specification.where(
                CaveSpecifications.hasId(id)
                        .and(CaveSpecifications.hasUserId(userId))
                        .and(CaveSpecifications.hasTreasures(treasure))
        );
        if (canEdit != null) {
            specification = specification.and(CaveSpecifications.hasCanEdit(canEdit));
        }

        return caveRepository.findAll(
                specification,
                PageRequest.of(offset, limit, sort.getSortValue())
        ).map(DragonCaveDTO::new);

    }


    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "Dragon")
    public ResponseEntity<String> updateCave(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonCave cave
    ) throws JsonProcessingException {
        DragonCave updatedCave = service.updateEntityWithUser(
                header,
                cave,
                cave.getId(),
                caveRepository::findById,
                DragonCave::getUser,
                (old, updates) -> {
                    old.setNumberOfTreasures(updates.getNumberOfTreasures());
                    old.setCanEdit(updates.getCanEdit());
                },
                caveRepository
        );
        String json = service.convertToJson(new DragonCaveDTO(updatedCave));
        return ResponseEntity.ok(json);
    }
}
