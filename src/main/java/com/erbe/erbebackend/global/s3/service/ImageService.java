package com.erbe.erbebackend.global.s3.service;

import com.erbe.erbebackend.global.s3.S3Uploader;
import com.erbe.erbebackend.global.s3.dto.ImageUploadResponse;
import com.erbe.erbebackend.global.s3.entity.ImageDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;

    public ImageUploadResponse uploadImage(MultipartFile file, ImageDirectory dirName) {
        String imageUrl = s3Uploader.upload(file, dirName);
        return ImageUploadResponse.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
