package org.zir.dragonieze.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zir.dragonieze.imphist.*;
import org.zir.dragonieze.minio.MinioService;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.user.Role;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;


@RestController
@RequestMapping("/dragon/import")
public class ImportHistoryController {
    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistorySpecification importHistorySpecification;
    private final MinioService minioService;

    public ImportHistoryController(ImportHistoryRepository importHistoryRepository, ImportHistorySpecification importHistorySpecification, MinioService minioService) {
        this.importHistoryRepository = importHistoryRepository;
        this.importHistorySpecification = importHistorySpecification;
        this.minioService = minioService;
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
    @Transactional
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        if (importHistoryRepository.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
        ImportHistory importHistory = importHistoryRepository.findById(id).get();

        String uniqueName = importHistory.getUniqueName();
        try {
            InputStream inputStream = minioService.downloadFile(uniqueName);

            byte[] fileContent = inputStream.readAllBytes();

            String contentType ;
            try {
                contentType = Files.probeContentType(Path.of(uniqueName));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + importHistory.getFileName() + "\"")
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

    }

}
