package com.imagetotextservice.imagetotextapi.controller;


import com.imagetotextservice.imagetotextapi.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class HomeController {

    @Value("${gcp.bucket.name}")
    private String bucketName;

    private final ImageService imageService;

    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }


    @GetMapping
    public String home() {
        return "home";
    }

    @PostMapping("/import")
    public String processImage(
            @RequestParam("photo")MultipartFile image,
            Model model
    ) throws IOException {

        String filename = image.getOriginalFilename();
        String mimeType = image.getContentType();
        System.out.println(mimeType);

        if (mimeType.equals("image/jpeg") || mimeType.equals("image/png")) {
            model.addAttribute("wrongFile", false);
            model.addAttribute("processing", true);

            String fileName = imageService.createFileName(image);

            String link = imageService.uploadImage(image, fileName);

            model.addAttribute("link", link);
            model.addAttribute("fileName", fileName);
            model.addAttribute("bucketName", bucketName);
        } else {
            model.addAttribute("wrongFile", true);
            model.addAttribute("processing", false);
        }


        return "home";
    }

}