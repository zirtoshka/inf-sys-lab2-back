package org.zir.dragonieze.imphist;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zir.dragonieze.minio.MinioService;
import org.zir.dragonieze.minio.UploadMinieException;
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

    @Around("@annotation(LogImportHistory)")
//    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Object logImportHistory(ProceedingJoinPoint joinPoint) throws Throwable {
        ImportHistory history = new ImportHistory();
        history.setStatus(StatusImport.IN_PROGRESS);

        try {
            setUserAndFileDetails(joinPoint, history);

            importHistoryService.saveImportHistory(history);
            Object result = joinPoint.proceed();

            updateHistoryAfterSuccess(result, history);
            if (history.getStatus() == StatusImport.IN_PROGRESS) {
                if (history.getImportedCount()==-1){
                    history.setStatus(StatusImport.FAILED);
            }else{
                history.setStatus(StatusImport.SUCCESS);}
            }
        } catch (UploadMinieException e) {
            history.setStatus(StatusImport.FAILED_UPLOAD_FILE);
            handleDownloadException(e, joinPoint, history);
            throw e;

        } catch (Exception ex) {
            history.setStatus(StatusImport.FAILED);
            history.setImportedCount(0);
//            importHistoryService.saveImportHistory(history);
            throw ex;
        } finally {
            try{
                importHistoryService.saveImportHistory(history); //todo
            }catch (CannotCreateTransactionException e){
                System.out.println("bebebeebebebebebebebe");
            }
        }

        return null;
    }


//    @Transactional(propagation = Propagation.MANDATORY)
    protected void uploadFileToMinio(MultipartFile file, String fileName) throws IOException, UploadMinieException {
        byte[] fileContent = file.getBytes();
        InputStream uploadStream = new ByteArrayInputStream(fileContent);
        minioService.uploadFile(
                BUCKET_NAME, fileName, uploadStream, file.getSize(), file.getContentType()
        );
    }

//    @Transactional(propagation = Propagation.MANDATORY)
    protected void setUserAndFileDetails(ProceedingJoinPoint joinPoint, ImportHistory history) throws Exception, UploadMinieException {
        OpenAmUserPrincipal user = (OpenAmUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            history.setUserId(user.getUser().getId());
        }

        Object[] args = joinPoint.getArgs();
        MultipartFile file = (MultipartFile) args[1];
        history.setFileName(file.getOriginalFilename());

        String uniqueFileName = UUID.randomUUID().toString();
        history.setUniqueName(uniqueFileName);
        try {
            uploadFileToMinio(file, uniqueFileName);
        } catch (UploadMinieException e) {
            history.setStatus(StatusImport.FAILED_UPLOAD_FILE);
            throw e;
        }
    }
//    @Transactional(propagation = Propagation.MANDATORY)
    protected void updateHistoryAfterSuccess(Object result, ImportHistory history) throws Exception{
        if (result instanceof ResponseEntity<?> responseEntity) {
            Object body = responseEntity.getBody();
            if (body instanceof Map<?, ?> map && map.containsKey("importedCount")) {
                history.setImportedCount((Integer) map.get("importedCount"));
            }
        }

        if (result instanceof ResponseEntity<?> responseEntity) {
            Object body = responseEntity.getBody();
            if (body instanceof Map<?, ?> map && map.containsKey("message")) {
                if (map.get("message").toString().contains("Failed")) {
                    throw new Exception("Failed");
                }
            }
        }

    }

//    @Transactional(propagation = Propagation.MANDATORY)
    protected void handleDownloadException(UploadMinieException e, ProceedingJoinPoint joinPoint, ImportHistory history) throws Throwable {
        Object result = joinPoint.proceed();
        updateHistoryAfterSuccess(result, history);
    }


}
