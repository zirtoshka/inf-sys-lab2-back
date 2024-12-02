package org.zir.dragonieze.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.admin.UpdateAppStatusRequest;
import org.zir.dragonieze.dragon.DragonCave;
import org.zir.dragonieze.dragon.repo.AppRepository;
import org.zir.dragonieze.admin.StatusApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dto.ApplicationDTO;
import org.zir.dragonieze.dto.DragonCaveDTO;
import org.zir.dragonieze.services.AdminService;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.CaveSort;
import org.zir.dragonieze.sort.specifications.ApplicationSpecifications;
import org.zir.dragonieze.sort.specifications.CaveSpecifications;
import org.zir.dragonieze.user.User;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("dragon/app")
public class ApplicationController extends Controller {
    private final AppRepository appRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AdminService adminService;



    public ApplicationController(BaseService baseService, AppRepository appRepository, SimpMessagingTemplate messagingTemplate, AdminService adminService) {
        super(baseService);
        this.appRepository = appRepository;
        this.messagingTemplate = messagingTemplate;
        this.adminService = adminService;
    }

    @GetMapping("/add")
    public ResponseEntity<String> newApp(
            @RequestHeader(HEADER_AUTH) String header
    ) {
        User user = service.getUserFromHeader(header);
        AdminApplication newApp = AdminApplication.builder()
                .createdAt(LocalDateTime.now())
                .status(StatusApplication.NEW)
                .user(user)
                .build();
        appRepository.save(newApp);

        messagingTemplate.convertAndSend("/topic/application", Map.of(
                "action", "ADD",
                "application", new ApplicationDTO(newApp))
        ); //todo subscribe topic
        return new ResponseEntity<>("New app created", HttpStatus.CREATED);
    }

    @PostMapping("/changeStatus")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> changeApplicationStatus(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestBody UpdateAppStatusRequest request
    ) {
        try {
            adminService.changeApplicationStatus(request);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("{\"message\": \"" + request + "\"}", HttpStatus.OK);
    }



    @GetMapping("/get")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<ApplicationDTO> getApplications(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") CaveSort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "status", required = false) StatusApplication status,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "createdAt", required = false) String createdAt
    ) {
        Specification<AdminApplication> specification = Specification.where(
                ApplicationSpecifications.hasId(id)
                        .and(ApplicationSpecifications.hasUserId(userId))
                        .and(ApplicationSpecifications.hasStatus(status))
                        .and(ApplicationSpecifications.hasCreatedAt(createdAt))
        );


        return appRepository.findAll(
                specification,
                PageRequest.of(offset, limit, sort.getSortValue())
        ).map(ApplicationDTO::new);

    }


}
