package com.cherry.manage;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cherry
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "minio.client")
public class MinIOConfig {
    
    private String endpoint;
    private String fileHost;
    private String bucketName;
    private String accessKey;
    private String secretKey;
    
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    }
}
