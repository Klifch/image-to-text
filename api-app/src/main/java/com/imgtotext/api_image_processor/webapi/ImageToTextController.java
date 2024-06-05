package com.imgtotext.api_image_processor.webapi;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.imgtotext.api_image_processor.configuration.GoogleCloudConfig;
import com.imgtotext.api_image_processor.dto.ImageResponseDto;
import com.imgtotext.api_image_processor.service.VisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/text-from-image")
@CrossOrigin(origins = "http://localhost:8080") // Allowing only localhost:8080
public class ImageToTextController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudConfig.class);

    private final VisionService visionService;

    public ImageToTextController(VisionService visionService) {
        this.visionService = visionService;
    }

    @GetMapping
    public ResponseEntity<ImageResponseDto> imageToText(
            @RequestParam("bucket") String bucketName,
            @RequestParam("image") String imageName
    ) {
        logger.info("API call received with params: {} and {}", bucketName, imageName);
        try {
            AnnotateImageResponse response = visionService.extractTextFromImage(bucketName, imageName);
            String text = response.getFullTextAnnotation().getText();

            return ResponseEntity.ok(new ImageResponseDto(text));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
