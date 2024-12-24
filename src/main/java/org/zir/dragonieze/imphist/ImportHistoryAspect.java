package org.zir.dragonieze.imphist;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;

import java.util.Map;

@Aspect
@Component
public class ImportHistoryAspect {
    private final ImportHistoryService importHistoryService;


    public ImportHistoryAspect(ImportHistoryService importHistoryService) {
        this.importHistoryService = importHistoryService;
    }

    @Around("@annotation(LogImportHistory)")
    public Object logImportHistory(ProceedingJoinPoint joinPoint) throws Throwable {
        ImportHistory history = new ImportHistory();
        history.setStatus(StatusImport.IN_PROGRESS);

        OpenAmUserPrincipal user = null;
        try {
            user = (OpenAmUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user != null) {
                history.setUserId(user.getUser().getId());
            }
            importHistoryService.saveImportHistory(history);
            Object result = joinPoint.proceed();
            history.setStatus(StatusImport.SUCCESS);
            if (result instanceof ResponseEntity<?> responseEntity) {
                Object body = responseEntity.getBody();
                if (body instanceof Map<?, ?> map && map.containsKey("importedCount")) {
                    history.setImportedCount((Integer) map.get("importedCount"));
                }
            }
        } catch (Exception ex) {
            history.setStatus(StatusImport.FAILED);
            history.setImportedCount(0);
            importHistoryService.saveImportHistory(history);
            throw ex;
        } finally {
            importHistoryService.saveImportHistory(history);
        }

        return null;
    }


}
