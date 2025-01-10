package org.zir.dragonieze.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${m.password}")
    private String password;

    @Value("${m.u}")
    private String user;

    @Value("${m.url}")
    private String url;

    public final static String BUCKET_NAME = "buckets";

    @Bean
    public MinioClient minioClient() {

        return MinioClient.builder()
                .endpoint(url)
                .credentials(user, password)
                .build();
    }
}
