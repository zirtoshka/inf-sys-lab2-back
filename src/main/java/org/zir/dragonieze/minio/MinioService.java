package org.zir.dragonieze.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.zir.dragonieze.minio.MinioConfig.BUCKET_NAME;


@Service
public class MinioService {

    private final MinioClient minioClient;


    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

//    @Transactional(propagation = Propagation.MANDATORY)
    public void uploadFile(String bucketName, String fileName, InputStream inputStream, long size, String contentType) throws UploadMinieException {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadMinieException("Failed to upload file to MinIO", e);
        }
    }

    public InputStream downloadFile(String fileName) throws DownloadMinioException {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new DownloadMinioException(e);
        }
    }

}
