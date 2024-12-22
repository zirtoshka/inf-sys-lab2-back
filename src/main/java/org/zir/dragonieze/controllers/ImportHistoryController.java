package org.zir.dragonieze.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zir.dragonieze.imphist.ImportHistory;
import org.zir.dragonieze.imphist.ImportHistoryRepository;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.user.Role;

import java.util.List;

@RestController
@RequestMapping("/dragon/import")
public class ImportHistoryController {
    private final ImportHistoryRepository importHistoryRepository;

    public ImportHistoryController(ImportHistoryRepository importHistoryRepository) {
        this.importHistoryRepository = importHistoryRepository;
    }


    @GetMapping("/history")
    public ResponseEntity<List<ImportHistory>> getImportHistory(
            @AuthenticationPrincipal OpenAmUserPrincipal user
    ) {
        List<ImportHistory> history;
        if (user.hasRole(Role.ADMIN)) {
            history = importHistoryRepository.findAll();
        } else {
            history = importHistoryRepository.findByUserId(user.getUser().getId());
        }
        return ResponseEntity.ok(history);
    }

}
