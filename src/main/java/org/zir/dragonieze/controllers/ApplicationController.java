package org.zir.dragonieze.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.admin.UpdateAppStatusRequest;
import org.zir.dragonieze.dragon.repo.AppRepository;
import org.zir.dragonieze.admin.StatusApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dto.ApplicationDTO;

import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.AdminService;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.sort.CaveSort;
import org.zir.dragonieze.sort.specifications.ApplicationSpecifications;
import org.zir.dragonieze.user.User;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("dragon/app")
public class ApplicationController extends Controller {
    private final AppRepository appRepository;
    private final AdminService adminService;


    public ApplicationController(BaseService baseService, AppRepository appRepository, SimpMessagingTemplate messagingTemplate, AdminService adminService) {
        super(baseService, messagingTemplate);
        this.appRepository = appRepository;
        this.adminService = adminService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> newApp(
            @AuthenticationPrincipal OpenAmUserPrincipal principal
    ) {
        User user = principal.getUser();
        AdminApplication newApp = AdminApplication.builder()
                .createdAt(LocalDateTime.now())
                .status(StatusApplication.NEW)
                .user(user)
                .build();
        appRepository.save(newApp);

        messagingTemplate.convertAndSend("/topic/applications", Map.of(
                "action", "ADD",
                "data", new ApplicationDTO(newApp))
        );
        return new ResponseEntity<>("{\"message\": \"New app created\"}", HttpStatus.CREATED);
    }

    @PostMapping("/changeStatus")
    public ResponseEntity<String> changeApplicationStatus(
            @RequestBody UpdateAppStatusRequest request,
            @AuthenticationPrincipal OpenAmUserPrincipal admin
    ) {
        try {
            adminService.changeApplicationStatus(admin, request);
            ApplicationDTO app = new ApplicationDTO(appRepository.findById(request.getId()).get());

            messagingTemplate.convertAndSend("/topic/applications", Map.of(
                    "action", "UPDATE",
                    "data", app)
            );
            return new ResponseEntity<>("{\"message\": \"" + app.getStatus() + "\"}", HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/get")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<ApplicationDTO> getApplications(
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
