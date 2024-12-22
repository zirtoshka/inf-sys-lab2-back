package org.zir.dragonieze.imphist;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ImportHistoryAspect {
    private final ImportHistoryRepository importHistoryRepository;

    public ImportHistoryAspect(ImportHistoryRepository importHistoryRepository) {
        this.importHistoryRepository = importHistoryRepository;
    }

    @Around("@annotation(LogImportHistory)")
    public Object logImportHistory(ProceedingJoinPoint joinPoint) throws Throwable {
        ImportHistory history = new ImportHistory();
        history.setStatus(StatusImport.IN_PROGRESS);

//         OpenAmUserPrincipal user =

        try {
            importHistoryRepository.save(history);
            Object result = joinPoint.proceed();
            history.setStatus(StatusImport.SUCCESS);
            if (result instanceof Integer) {
                history.setImportedCount((Integer) result);
            }
        } catch (Exception ex) {
            history.setStatus(StatusImport.FAILED);
            history.setImportedCount(0);
            throw ex;
        } finally {
            importHistoryRepository.save(history);
        }

        return null;
    }
}
