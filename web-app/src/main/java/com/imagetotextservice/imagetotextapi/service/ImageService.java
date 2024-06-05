package com.imagetotextservice.imagetotextapi.service;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class ImageService {

    @Value("${gcp.bucket.name}")
    private String bucketName;

    private final Storage storage;

    public ImageService(Storage storage) {
        this.storage = storage;
    }

    public String createFileName(MultipartFile file) {

        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }

    public String uploadImage(MultipartFile file, String fileName) throws IOException {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        System.out.println(fileName);

        storage.create(blobInfo, file.getBytes(), Storage.BlobTargetOption.doesNotExist());

        URL signedUrl = storage.signUrl(
                blobInfo,
                15,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature()
        );

        return signedUrl.toString();
    }

}
