package com.github.solanej.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@ConfigurationProperties("oss")
public class OSSConfig {

    /* 访问密钥 */
    private final String ACCESS_KEY = "TvzknGD9FEguVOCemRct";
    private final String SECRET_KEY = "5DPl2ZWXmRLGbfMivOINCu7H8nEcgtSe49AwqoT6";
    /* 访问地址 */
    @Value("${OSS_ENDPOINT}")
    public String ENDPOINT;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(ENDPOINT))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .forcePathStyle(true) // 关键配置！RustFS 需启用 Path-Style
                .region(Region.CN_NORTH_1)
                .build();
    }
}
