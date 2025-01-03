package org.zir.dragonieze.imphist;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.zir.dragonieze.minio.MinioService;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static org.zir.dragonieze.minio.MinioConfig.BUCKET_NAME;

@Aspect
@Component
public class ImportHistoryAspect {

    private final ImportHistoryService importHistoryService;
    private final MinioService minioService;


    public ImportHistoryAspect(ImportHistoryService importHistoryService, MinioService minioService) {
        this.importHistoryService = importHistoryService;
        this.minioService = minioService;
    }

    //todo add transactions
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

            Object[] args = joinPoint.getArgs();
            MultipartFile file = (MultipartFile) args[1];
            System.out.println("Original file name: " + file.getOriginalFilename());
            history.setFileName(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString();
            history.setUniqueName(uniqueFileName);

            uploadFileToMinio(file, uniqueFileName);

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


    private void uploadFileToMinio(MultipartFile file, String fileName) throws IOException {
        byte[] fileContent = file.getBytes();
        InputStream uploadStream = new ByteArrayInputStream(fileContent);
         minioService.uploadFile(
                BUCKET_NAME, fileName, uploadStream, file.getSize(), file.getContentType()
        );
    }

}
