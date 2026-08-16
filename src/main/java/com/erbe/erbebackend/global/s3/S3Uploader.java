package com.erbe.erbebackend.global.s3;

import com.erbe.erbebackend.global.exception.CustomException;
import com.erbe.erbebackend.global.s3.entity.ImageDirectory;
import com.erbe.erbebackend.global.s3.exception.S3ErrorCode;
import com.erbe.erbebackend.infrastructure.openai.service.OpenAiProductAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;
    private final OpenAiProductAnalysisService openAiProductAnalysisService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String upload(MultipartFile file, ImageDirectory dirName) {
        if (file.isEmpty()) {
            throw new CustomException(S3ErrorCode.S3_EMPTY_FILE);
        }

        if(dirName.name().equals("FEED") && !(openAiProductAnalysisService.isMCMProduct(file))){
            throw new CustomException(S3ErrorCode.S3_NOT_MCM);
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

        byte[] decodedBytes;

        try {
                decodedBytes = Base64.getDecoder().decode(b64);
            } catch (IllegalArgumentException e) {
                throw new CustomException(S3ErrorCode.S3_UPLOAD_FAILED);
            }

        InputStream inputStream = new ByteArrayInputStream(decodedBytes);

        String fileName = directory.name().toLowerCase() + "/" + UUID.randomUUID() + ".png";

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
            throw new CustomException(S3ErrorCode.S3_UPLOAD_FAILED);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    public String delete(String fullURL){

        String decodedUrl = URLDecoder.decode(fullURL, StandardCharsets.UTF_8);

        // 버킷 base URL 검증
        String expectedPrefix = String.format("https://%s.s3.", bucket);
        if (!decodedUrl.startsWith(expectedPrefix)) {
            throw new CustomException(S3ErrorCode.S3_INVALID_URL);
        }

        String objectKey = decodedUrl.substring(decodedUrl.indexOf(".com/") + 5);

        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(objectKey)
                            .build());
        } catch (Exception e) {
            throw new CustomException(S3ErrorCode.S3_DELETE_FAILED);
        }
        return "파일 삭제 성공";
    }
}
