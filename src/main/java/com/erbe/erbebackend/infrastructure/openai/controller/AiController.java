package com.erbe.erbebackend.infrastructure.openai.controller;

import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import com.erbe.erbebackend.infrastructure.openai.dto.request.ImageGenRequest;
import com.erbe.erbebackend.infrastructure.openai.dto.response.AnalysisResponse;
import com.erbe.erbebackend.infrastructure.openai.dto.response.ImageGenResponse;
import com.erbe.erbebackend.infrastructure.openai.service.OpenAiImageGenService;
import com.erbe.erbebackend.infrastructure.openai.service.OpenAiTravelAnalysisService;
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
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "AI 관련 API")
public class AiController {

    private final OpenAiTravelAnalysisService travelAnalysisService;

    private final OpenAiImageGenService imageGenService;

    @Operation(summary = "여행 분석 API", description = "유저의 현재까지의 여행을 기반으로 분석하는 API")
    @GetMapping("/analysis")
    public ResponseEntity<BaseResponse<AnalysisResponse>> getTravelAnalysis(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ){
        String answer = travelAnalysisService.getResult(customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "여행 분석 완료", AnalysisResponse.of(answer)));
    }

    @Operation(summary = "이미지 업로드 API", description = "유저가 선택한 사진을 기반으로 트래블패치를 제작하는 API")
    @PostMapping("/imageGen")
    public ResponseEntity<BaseResponse<ImageGenResponse>> getImageGen(
            @RequestBody ImageGenRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ){
        List<String> answer = imageGenService.getResult(request, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "이미지 생성 완료", ImageGenResponse.of(answer)));
    }

}
