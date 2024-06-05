package com.imgtotext.api_image_processor.dto;

public class ImageResponseDto {

    private String text;

    public ImageResponseDto() {
    }

    public ImageResponseDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
