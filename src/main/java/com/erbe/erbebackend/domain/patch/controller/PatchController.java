package com.erbe.erbebackend.domain.patch.controller;

import com.erbe.erbebackend.domain.patch.dto.request.PatchApplyRequest;
import com.erbe.erbebackend.domain.patch.dto.request.PatchSaveRequest;
import com.erbe.erbebackend.domain.patch.dto.response.PatchApplyResponse;
import com.erbe.erbebackend.domain.patch.dto.response.PatchListResponse;
import com.erbe.erbebackend.domain.patch.dto.response.PatchSaveResponse;
import com.erbe.erbebackend.domain.patch.enums.PatchType;
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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam PatchType type) {

        // service 호출
        List<PatchListResponse> response = patchService.patchList(userDetails.getId(), type);

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "패치 리스트 조회 성공", response));
    }

    // 패치 삭제
    @Operation(summary = "패치 삭제 API", description = "사용자가 패치를 삭제할때 사용하는 API")
    @DeleteMapping("/patches/{patch-id}")
    public ResponseEntity<BaseResponse<Void>> deletePatch(
            @PathVariable("patch-id") Long patchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        patchService.patchDelete(patchId, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "패치 삭제 성공", null));
    }

    // 패치 적용
    @Operation(summary = "패치 적용 API", description = "사용자 본인 소유 가방에 패치를 3D로 적용후 저장하는 API")
    @PostMapping("/patches/positions")
    public ResponseEntity<BaseResponse<PatchApplyResponse>> applyPatch(
            @Valid @RequestBody PatchApplyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        PatchApplyResponse response = patchService.applyPatch(request, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(201, "패치 적용 후 저장 성공", response));
    }
}
