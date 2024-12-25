package org.zir.dragonieze.minio;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://miniooooooo:9000")
                .credentials("user", "password")
                .build();
    }
}
