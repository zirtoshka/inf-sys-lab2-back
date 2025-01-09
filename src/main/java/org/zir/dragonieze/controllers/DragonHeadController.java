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
import org.zir.dragonieze.dragon.DragonHead;
import org.zir.dragonieze.dragon.repo.DragonHeadRepository;
import org.zir.dragonieze.dto.DragonCaveDTO;
import org.zir.dragonieze.dto.DragonHeadDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.HeadSort;
import org.zir.dragonieze.sort.specifications.HeadSpecifications;

import java.util.Map;

@RestController
@RequestMapping("/dragon/head")
public class DragonHeadController extends Controller {
    private final DragonHeadRepository headRepository;
    private final HeadSpecifications headSpecifications;

    public DragonHeadController(BaseService service, DragonHeadRepository headRepository, SimpMessagingTemplate messagingTemplate, HeadSpecifications headSpecifications) {
        super(service, messagingTemplate);
        this.headRepository = headRepository;
        this.headSpecifications = headSpecifications;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addHead(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody DragonHead head
    ) throws Exception {
        DragonHead savedHead = service.saveEntityWithUser(user, head, DragonHead::setUser, headRepository);
        messagingTemplate.convertAndSend("/topic/heads", Map.of(
                "action", "ADD",
                "data", new DragonHeadDTO(savedHead))
        );
        String json = service.convertToJson(new DragonHeadDTO(savedHead));
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "DragonHead")
    public ResponseEntity<String> deleteHead(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                user,
                id,
                DragonHead::getUser,
                headRepository
        );
        messagingTemplate.convertAndSend("/topic/heads", Map.of(
                "action", "DELETE",
                "id", id)
        );
        return ResponseEntity.ok(
                "'was deleted': " + id
        );
    }

    @GetMapping("/get")
    public Page<DragonHeadDTO> getHeads(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") HeadSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit,
            @RequestParam(value = "eyesCount", required = false) Double eyesCount,
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        Specification<DragonHead> specification = Specification.where(
                headSpecifications.hasId(id)
                        .and(headSpecifications.hasEyes(eyesCount))
                        .and(headSpecifications.hasUserId(userId))
        );
        specification = canEditSpec(canEdit, specification, headSpecifications);
        return headRepository.findAll(specification,
                        PageRequest.of(offset, limit, sort.getSortValue()))
                .map(DragonHeadDTO::new);
    }


    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "DragonHead")
    public ResponseEntity<String> updateHead(
            @AuthenticationPrincipal OpenAmUserPrincipal user,
            @Valid @RequestBody DragonHead head
    ) throws JsonProcessingException {

        DragonHead updateHead = service.updateEntityWithUser(
                user,
                head,
                head.getId(),
                headRepository::findById,
                DragonHead::getUser,
                (old, updates) -> {
                    old.setEyesCount(updates.getEyesCount());
                    old.setCanEdit(updates.getCanEdit());
                },
                headRepository
        );
        messagingTemplate.convertAndSend("/topic/heads", Map.of(
                "action", "UPDATE",
                "data", new DragonHeadDTO(updateHead))
        );
        String json = service.convertToJson(new DragonHeadDTO(updateHead));
        return ResponseEntity.ok(json);
    }

}
