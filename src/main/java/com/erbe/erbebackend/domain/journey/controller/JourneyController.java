package com.erbe.erbebackend.domain.journey.controller;

import com.erbe.erbebackend.domain.journey.dto.response.JourneyMapPinResponse;
import com.erbe.erbebackend.domain.journey.dto.response.JourneyResponse;
import com.erbe.erbebackend.domain.journey.service.JourneyService;
import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/journeys")
@Tag(name = "Journey", description = "여행 관련 API")
public class JourneyController {

    private final JourneyService journeyService;

    @Operation(summary = "단일 여행 조회 API", description = "여행 ID 기반으로, 단일 여행 정보를 조회하는 API")
    @GetMapping("/{journeyId}")
    public ResponseEntity<BaseResponse<JourneyResponse>> getJourney(@PathVariable Long journeyId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        JourneyResponse response = journeyService.findJourneyById(journeyId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "단일 여행 조회 성공",response));
    }

    @Operation(summary = "여행 전체 조회 API", description = "사용자 기반으로, 사용자의 모든 여행 정보를 조회하는 API")
    @GetMapping
    public ResponseEntity<BaseResponse<List<JourneyResponse>>> getJourneys(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<JourneyResponse> responseList = journeyService.findAllJourneys(customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "여행 리스트 조회 성공", responseList));
    }

    @Operation(summary = "지도 핀 조회 API", description = "사용자 기반으로, 지도에 핀을 위치시키기 위해 여행ID + 좌표값만 조회하는 API")
    @GetMapping("/pins")
    public ResponseEntity<BaseResponse<List<JourneyMapPinResponse>>> getMapPins(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<JourneyMapPinResponse> responseList = journeyService.getMapPins(customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "지도 맵 핀 조회 성공", responseList));
    }
}
