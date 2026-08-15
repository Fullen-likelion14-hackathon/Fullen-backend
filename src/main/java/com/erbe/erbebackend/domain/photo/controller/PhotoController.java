package com.erbe.erbebackend.domain.photo.controller;

import com.erbe.erbebackend.domain.photo.dto.response.PhotoResponse;
import com.erbe.erbebackend.domain.photo.service.PhotoService;
import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Photo", description = "사진 관련 API")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/photos")
    public ResponseEntity<BaseResponse<List<PhotoResponse>>> getUserPhoto(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ){

        List<PhotoResponse> responseList = photoService.getUserPhoto(customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "유저 기반 사진 전체 조회 성공", responseList));
    }
}
