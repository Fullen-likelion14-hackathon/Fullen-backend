package com.erbe.erbebackend.domain.photo.controller;

import com.erbe.erbebackend.domain.nation.enums.Continent;
import com.erbe.erbebackend.domain.photo.dto.response.PhotoDetailResponse;
import com.erbe.erbebackend.domain.photo.dto.response.PhotoResponse;
import com.erbe.erbebackend.domain.photo.service.PhotoService;
import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Photo", description = "사진 관련 API")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/photos")
    @Operation(summary = "유저별 사진 전체 조회 API", description = "유저의 모든 사진을 조회하는 API입니다.")
    public ResponseEntity<BaseResponse<List<PhotoResponse>>> getUserPhoto(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) Continent scope
            ){

        List<PhotoResponse> responseList = photoService.getUserPhoto(scope, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "유저 기반 사진 전체 조회 성공", responseList));
    }

    @GetMapping("/photos/{photoId}")
    @Operation(summary = "사진 조회 API", description = "사진 상세 조회 API입니다.")
    public ResponseEntity<BaseResponse<PhotoDetailResponse>> getPhotoDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long photoId
    ){
        PhotoDetailResponse response = photoService.getPhotoDetail(photoId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "사진 상세 조회 성공", response));
    }

    @DeleteMapping("/photos/{photoId}")
    @Operation(summary = "사진 삭제 API", description = "유저의 사진을 삭제하는 API입니다.")
    public ResponseEntity<BaseResponse<String>> deletePhoto(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long photoId
    ){
        String response = photoService.deletePhoto(photoId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "사진 삭제 성공", response));
    }
}
