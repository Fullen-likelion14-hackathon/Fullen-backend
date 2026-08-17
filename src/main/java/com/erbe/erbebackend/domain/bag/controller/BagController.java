package com.erbe.erbebackend.domain.bag.controller;

import com.erbe.erbebackend.domain.bag.dto.response.UserBagListResponse;
import com.erbe.erbebackend.domain.bag.service.BagService;
import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Bag", description = "가방 관련 API")
public class BagController {

    private final BagService bagService;

    // 사용자 소유 가방 리스트 조회
    @Operation(summary = "소유한 가방 리스트 조회 API", description = "사용자가 소유한 가방 리스트 목록을 조회하는 API")
    @GetMapping("/bags")
    public ResponseEntity<BaseResponse<List<UserBagListResponse>>> userBagList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        List<UserBagListResponse> response = bagService.findAllUserBags(userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "사용자가 소유한 가방 리스트 조회 성공", response));
    }
}
