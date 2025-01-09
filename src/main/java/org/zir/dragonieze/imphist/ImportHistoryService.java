package org.zir.dragonieze.imphist;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
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
//    private final JtaTransactionManager jtaTransactionManager;


    public ImportHistoryService(ImportHistoryRepository importHistoryRepository, PersonService personService, MinioService minioService) {
        this.importHistoryRepository = importHistoryRepository;
        this.personService = personService;
        this.minioService = minioService;
//        this.jtaTransactionManager = jtaTransactionManager;
    }

    public ResponseEntity<Map<String, Object>> importPerson(OpenAmUserPrincipal user,
                                                            MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Failed File is empty",
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
            e.printStackTrace();
            statusImport = StatusImport.FAILED;
        } catch (UploadMinieException e) {
            e.printStackTrace();
            statusImport = StatusImport.FAILED_UPLOAD_FILE;
        }


        try {
            savedPersons = savePersonDB(file, user);
            if (statusImport != StatusImport.FAILED_UPLOAD_FILE) {
                statusImport = StatusImport.SUCCESS;
            }
            count = savedPersons.size();
        } catch (Exception e) {
            e.printStackTrace();
            //rollback for savePersonDB()
            count = 0;
            statusImport = StatusImport.FAILED;
        }


        try {
            saveImportHistory(count, statusImport, filename, user.getUser().getId(), uniqueFileName);
        } catch (CannotCreateTransactionException e) {
            e.printStackTrace();
            //bd отказало
        }


        return ResponseEntity.ok(Map.of(
                "message", "Successfully imported persons",
                "importedCount", count));
    }

    protected void uploadFileToMinio(MultipartFile file, String fileName) throws IOException, UploadMinieException {
        byte[] fileContent = file.getBytes();
        InputStream uploadStream = new ByteArrayInputStream(fileContent);
        minioService.uploadFile(
                BUCKET_NAME, fileName, uploadStream, file.getSize(), file.getContentType()
        );
    }

    protected List<Person> savePersonDB(MultipartFile file, OpenAmUserPrincipal user) throws Exception {
        List<Person> persons = personService.parsePersonsFromFile(file);

        persons = persons.stream()
                .map(personService::preparePerson)
                .toList();
        List<Person> savedPersons = personService.savePersons(persons, user);
        return savedPersons;
    }


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
