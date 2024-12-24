package org.zir.dragonieze.imphist;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportHistoryService {
    private final ImportHistoryRepository importHistoryRepository;

    public ImportHistoryService(ImportHistoryRepository importHistoryRepository) {
        this.importHistoryRepository = importHistoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveImportHistory(ImportHistory history) {
        importHistoryRepository.save(history);
    }
}
