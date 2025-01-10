package org.zir.dragonieze.imphist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zir.dragonieze.dragon.Person;
import org.zir.dragonieze.dragon.repo.PersonRepository;
import org.zir.dragonieze.minio.MinioService;
import org.zir.dragonieze.minio.UploadMinieException;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.PersonService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.zir.dragonieze.minio.MinioConfig.BUCKET_NAME;

@Service
public class ImportHistoryService {
    private final ImportHistoryRepository importHistoryRepository;
    private final PersonService personService;
    private final MinioService minioService;


    public ImportHistoryService(ImportHistoryRepository importHistoryRepository, PersonService personService, MinioService minioService) {
        this.importHistoryRepository = importHistoryRepository;
        this.personService = personService;
        this.minioService = minioService;
    }

    public ResponseEntity<Map<String, Object>> importPerson(OpenAmUserPrincipal user,
                                                            MultipartFile file) {
        String res = "";
        if (file.isEmpty()) {
            res += "Failed File is empty";
            return ResponseEntity.badRequest().body(Map.of(
                    "message", res,
                    "importedCount", 0
            ));
        }

        String uniqueFileName = UUID.randomUUID().toString();
        String filename = file.getOriginalFilename();
        StatusImport statusImport = StatusImport.IN_PROGRESS;
        int count = 0;
        List<Person> savedPersons;

        try {
            uploadFileToMinio(file, uniqueFileName);
        } catch (IOException e) {
            res += "file is so strange; ";
            statusImport = StatusImport.FAILED;
        } catch (UploadMinieException e) {
            res += "uploading file to minio failed; ";
            statusImport = StatusImport.FAILED_UPLOAD_FILE;
        }


        try {
            savedPersons = personService.savePersonDB(file, user);
            if (statusImport != StatusImport.FAILED_UPLOAD_FILE) {
                statusImport = StatusImport.SUCCESS;
            }
            res+="Successfully imported persons";
            count = savedPersons.size();
        } catch (Exception e) {
            System.out.println("Error importing persons");
            e.printStackTrace();
            res += "importing person failed; ";
            statusImport = StatusImport.FAILED;
        }


        try {
            saveImportHistory(count, statusImport, filename, user.getUser().getId(), uniqueFileName);
        } catch (CannotCreateTransactionException e) {
            res += "failed to create transaction; ";
            //bd отказало
        }

        if (statusImport == StatusImport.SUCCESS || statusImport == StatusImport.FAILED_UPLOAD_FILE) {
            return ResponseEntity.ok(Map.of(
                    "message", res,
                    "importedCount", count));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", res,
                "importedCount", count));
    }

    protected void uploadFileToMinio(MultipartFile file, String fileName) throws UploadMinieException, IOException {
        byte[] fileContent = file.getBytes();
        InputStream uploadStream = new ByteArrayInputStream(fileContent);
        minioService.uploadFile(
                BUCKET_NAME, fileName, uploadStream, file.getSize(), file.getContentType()
        );
    }




    //todo add retry
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    protected void saveImportHistory(int count,
                                     StatusImport statusImport,
                                     String filename,
                                     Long userId,
                                     String uniqueName
    ) {
        ImportHistory history = new ImportHistory();
        history.setImportedCount(count);
        history.setUserId(userId);
        history.setStatus(statusImport);
        history.setFileName(filename);
        history.setUniqueName(uniqueName);
        importHistoryRepository.save(history);
    }
}
