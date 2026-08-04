package com.erbe.erbebackend.global.s3.controller;

import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.s3.dto.ImageUploadResponse;
import com.erbe.erbebackend.global.s3.entity.ImageDirectory;
import com.erbe.erbebackend.global.s3.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Image", description = "이미지 업로드 관련 API")
public class S3Controller {

    private final ImageService imageService;

    @Operation(summary = "이미지 업로드 API", description = "이미지를 업로드하고 URL을 리턴받는 API")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<ImageUploadResponse>> uploadImage(
            @RequestParam ImageDirectory dirName,
            @RequestParam MultipartFile file) {

        // service 호출
        ImageUploadResponse response = imageService.uploadImage(file, dirName);

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "이미지 업로드 성공", response));
    }
}
