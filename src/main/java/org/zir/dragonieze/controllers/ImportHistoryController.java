package org.zir.dragonieze.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zir.dragonieze.imphist.*;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.user.Role;


@RestController
@RequestMapping("/dragon/import")
public class ImportHistoryController {
    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistorySpecification importHistorySpecification;

    public ImportHistoryController(ImportHistoryRepository importHistoryRepository, ImportHistorySpecification importHistorySpecification) {
        this.importHistoryRepository = importHistoryRepository;
        this.importHistorySpecification = importHistorySpecification;
    }


    @GetMapping("/history")
    public Page<ImportHistory> getImportHistory(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") @Min(0) @Max(100) Integer limit,
            @RequestParam(value = "sort", defaultValue = "ID_ASC") ImportHistorySort sort,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) StatusImport status,
            @RequestParam(value = "importedCount", required = false) Integer importedCount,
            @AuthenticationPrincipal OpenAmUserPrincipal user
    ) {
        Specification<ImportHistory> specification = Specification.where(
                importHistorySpecification.hasId(id)
                        .and(importHistorySpecification.hasStatus(status))
                        .and(importHistorySpecification.hasImportedCount(importedCount))
        );
        Page<ImportHistory> history;
        if (user.hasRole(Role.ADMIN)) {
            specification = specification.and(
                    importHistorySpecification.hasUserId(userId));
        } else {
            specification = specification.and(
                    importHistorySpecification.hasUserId(user.getUser().getId()));
        }
        history = importHistoryRepository.findAll(specification,
                PageRequest.of(offset, limit, sort.getSortValue()));
        return history;
    }



    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        ImportHistory importHistory = importHistoryRepository.findById(id).get();

        String fileUrl = "";
//                importHistory.getFileUrl(); //todo

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(
                fileUrl, HttpMethod.GET, null, byte[].class);

        byte[] fileBytes = response.getBody();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file\"")
                .body(fileBytes);
    }

}
