package com.imagetotextservice.imagetotextapi.webapi;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final RestTemplate restTemplate;


    public ProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/text-from-image")
    public ResponseEntity<String> proxyTextFromImage(
            @RequestParam("bucket") String bucketName,
            @RequestParam("image") String imageName
    ) {
        String app2Url = String.format("http://app2-service:8081/api/text-from-image?bucket=%s&image=%s", bucketName, imageName);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(app2Url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testProxy() {
        String app2url = "http://app2-service:8081/api/test";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(app2url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
