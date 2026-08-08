package com.erbe.erbebackend.domain.patch.controller;

import com.erbe.erbebackend.domain.patch.dto.request.PatchSaveRequest;
import com.erbe.erbebackend.domain.patch.dto.response.PatchListResponse;
import com.erbe.erbebackend.domain.patch.dto.response.PatchSaveResponse;
import com.erbe.erbebackend.domain.patch.service.PatchService;
import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Patch", description = "패치 관련 API")
public class PatchController {

    private final PatchService patchService;

    // 패치 저장 API
    @Operation(summary = "패치 저장 API", description = "사용자가 AI가 생성해준 패치를 저장하는 API")
    @PostMapping("/patches")
    public ResponseEntity<BaseResponse<PatchSaveResponse>> savePatch(
            @Valid @RequestBody PatchSaveRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        PatchSaveResponse response = patchService.savePatch(request, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(201, "패치 저장 성공", response));
    }

    // 패치 리스트 조회
    @Operation(summary = "패치 리스트 조회 API", description = "사용자가 가지고 있는 패치 리스트를 최신순으로 보여주는 API")
    @GetMapping("/patches")
    public ResponseEntity<BaseResponse<List<PatchListResponse>>> patchList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        List<PatchListResponse> response = patchService.patchList(userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "패치 리스트 조회 성공", response));
    }
}
