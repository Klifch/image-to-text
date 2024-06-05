package com.imgtotext.api_image_processor.service;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VisionService {


    private final CloudVisionTemplate cloudVisionTemplate;
    private final ResourceLoader resourceLoader;
    private final Storage storage;

    public VisionService(CloudVisionTemplate cloudVisionTemplate, ResourceLoader resourceLoader, Storage storage) {
        this.cloudVisionTemplate = cloudVisionTemplate;
        this.resourceLoader = resourceLoader;
        this.storage = storage;
    }

    public AnnotateImageResponse extractTextFromImage(String bucketName, String imageName) {
        Blob blob = storage.get(bucketName, imageName);

        if (blob == null) {
            throw new RuntimeException("No such object in the bucket");
        }

        String gsutilLink = String.format("gs://%s/%s", bucketName, imageName);

        AnnotateImageResponse response = cloudVisionTemplate.analyzeImage(
                resourceLoader.getResource(gsutilLink),
                Feature.Type.TEXT_DETECTION
        );

        return response;
    }


}
