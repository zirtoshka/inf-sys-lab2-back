package org.zir.dragonieze.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MinioService {

    private final MinioClient minioClient;


    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String uploadFile(String bucketName, String fileName, InputStream inputStream, long size, String contentType) throws IOException {
        System.out.println(fileName+" filename"+ bucketName+" size"+size);
        System.out.println(inputStream);
        System.out.println(contentType);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .method(Method.GET)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to upload file to MinIO", e);
        }
    }
}
