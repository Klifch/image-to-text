package com.imagetotextservice.imagetotextapi.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleCloudConfig {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudConfig.class);

    @Value("${spring.cloud.gcp.credentials.location}")
    private String credentialsPath;

    private final ResourceLoader resourceLoader;

    public GoogleCloudConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public Storage googleCloudStorage() throws IOException {
        Resource resource = resourceLoader.getResource(credentialsPath);

        if (!resource.exists()) {
            logger.error("Credentials file does not exist at location {}", credentialsPath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);
            logger.info("Google Cloud Storage Credentials persist at {}", credentials);

            return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        } catch (IOException e) {
            logger.error("Failed to load credentials from {}", credentialsPath, e);
            throw e;
        }
    }
}
