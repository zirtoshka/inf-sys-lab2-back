package org.zir.dragonieze.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {


    private final AuditLogService auditLogService;

    @Around("@annotation(auditable)")
    public Object logAudit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object res;
        try {
            res = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Auditing failed", e);
            throw e;
        }

        try {
            Object[] args = joinPoint.getArgs();
            Long entityId = null;
            Object entity = null;
            String header = null;
            for (Object arg : args) {
                if (arg instanceof Long) {
                    entityId = (Long) arg;
                } else if (arg != null && arg.getClass().getSimpleName().equalsIgnoreCase(auditable.entity())) {
                    entity = arg;
                } else if (arg instanceof String && ((String) arg).startsWith("Bearer ")) {
                    header = (String) arg;
                }
            }

            if (header != null) {
                auditLogService.logAction(
                        header,
                        auditable.entity(),
                        entityId != null ? entityId : getIdFromEntity(entity),
                        auditable.action(),
                        entity
                );
            }
        } catch (Exception e) {
            log.error("Auditing failed", e);
        }
        return res;
    }


    private Long getIdFromEntity(Object entity) {
        try {
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            log.error("Error when getting the entity ID", e);
            return null;
        }
    }
}
