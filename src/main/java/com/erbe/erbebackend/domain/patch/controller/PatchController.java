package com.erbe.erbebackend.domain.patch.controller;

import com.erbe.erbebackend.domain.patch.dto.request.PatchSaveRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
