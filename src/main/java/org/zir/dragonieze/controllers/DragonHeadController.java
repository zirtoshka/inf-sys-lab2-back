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
import org.zir.dragonieze.dragon.DragonHead;
import org.zir.dragonieze.dragon.repo.DragonHeadRepository;
import org.zir.dragonieze.dto.DragonHeadDTO;
import org.zir.dragonieze.log.Auditable;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.HeadSort;
import org.zir.dragonieze.sort.LocationSort;
import org.zir.dragonieze.sort.specifications.HeadSpecifications;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/head")
public class DragonHeadController extends Controller {
    private final DragonHeadRepository headRepository;

    public DragonHeadController(BaseService service, DragonHeadRepository headRepository) {
        super(service);
        this.headRepository = headRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addHead(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonHead head
    ) throws JsonProcessingException {
        DragonHead savedHead = service.saveEntityWithUser(header, head, DragonHead::setUser, headRepository);
        String json = service.convertToJson(new DragonHeadDTO(savedHead));
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @Auditable(action = "DELETE", entity = "DragonHead")
    public ResponseEntity<String> deleteHead(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) {
        service.deleteEntityWithCondition(
                header,
                id,
                DragonHead::getUser,
                headRepository
        );
        return ResponseEntity.ok("удалилось ура");
    }

    @GetMapping("/get")
    public Page<DragonHeadDTO> getHeads(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") HeadSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "canEdit", required = false) Boolean canEdit,
            @RequestParam(value = "eyesCount", required = false) double eyesCount,
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        Specification<DragonHead> specification = Specification.where(
                HeadSpecifications.hasId(id)
                        .and(HeadSpecifications.hasCanEdit(canEdit))
                        .and(HeadSpecifications.hasEyes(eyesCount))
                        .and(HeadSpecifications.hasUserId(userId))
        );
        return headRepository.findAll(specification,
                        PageRequest.of(offset, limit, sort.getSortValue()))
                .map(DragonHeadDTO::new);
    }


    @Transactional
    @PostMapping("/update")
    @Auditable(action = "UPDATE", entity = "DragonHead")
    public ResponseEntity<String> updateHead(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonHead head
    ) throws JsonProcessingException {
        DragonHead updateHead = service.updateEntityWithUser(
                header,
                head,
                head.getId(),
                headRepository::findById,
                DragonHead::getUser,
                (old, updated) -> {
                    old.setEyesCount(updated.getEyesCount());
                    old.setCanEdit(updated.getCanEdit());
                },
                headRepository
        );
        String json = service.convertToJson(new DragonHeadDTO(updateHead));
        return ResponseEntity.ok(json);
    }

}
