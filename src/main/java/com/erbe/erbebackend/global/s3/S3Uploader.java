package com.erbe.erbebackend.global.s3;

import com.erbe.erbebackend.global.exception.CustomException;
import com.erbe.erbebackend.global.s3.entity.ImageDirectory;
import com.erbe.erbebackend.global.s3.exception.S3ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String upload(MultipartFile file, ImageDirectory dirName) {
        if (file.isEmpty()) {
            throw new CustomException(S3ErrorCode.S3_EMPTY_FILE);
        }

        String ext = "";
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = dirName.name().toLowerCase() + "/" + UUID.randomUUID() + ext;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new CustomException(S3ErrorCode.S3_UPLOAD_FAILED);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    public String uploadBase64ImageToS3(String b64){

        ImageDirectory directory = ImageDirectory.TRAVEL_PATCH;

        byte[] decodedBytes = Base64.getDecoder().decode(b64);

        InputStream inputStream = new ByteArrayInputStream(decodedBytes);

        String fileName = directory.name().toLowerCase() + "/" + UUID.randomUUID();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType("image/png")
                            .build(),
                    RequestBody.fromInputStream(inputStream, decodedBytes.length)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);


    }
}
