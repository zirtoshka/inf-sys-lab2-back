package org.zir.dragonieze.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.zir.dragonieze.minio.MinioConfig.BUCKET_NAME;


@Service
public class MinioService {

    private final MinioClient minioClient;


    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void uploadFile(String bucketName, String fileName, InputStream inputStream, long size, String contentType) throws IOException {
        System.out.println(fileName + " filename" + bucketName + " size" + size);
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to upload file to MinIO", e);
        }
    }

    public String getFileUrl(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(fileName)
                        .method(Method.GET)
                        .expiry(7, TimeUnit.DAYS)
                        .build()
        );
    }
}
