package com.erbe.erbebackend.domain.order.controller;

import com.erbe.erbebackend.domain.order.dto.request.PatchOrderRequest;
import com.erbe.erbebackend.domain.order.dto.request.PremiumOrderRequest;
import com.erbe.erbebackend.domain.order.dto.response.PatchOrderResponse;
import com.erbe.erbebackend.domain.order.dto.response.PremiumOrderResponse;
import com.erbe.erbebackend.domain.order.service.OrderService;
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
@Tag(name = "Order", description = "주문 관련 API")
public class OrderController {

    private final OrderService orderService;

    // 패치 주문
    @Operation(summary = "패치 주문 API", description = "가방에 부착됐고 아직 주문이 안된 패치를 주문 요청하는 API")
    @PostMapping("/orders/patches")
    public ResponseEntity<BaseResponse<PatchOrderResponse>> patchOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PatchOrderRequest request) {

        // service 호출
        PatchOrderResponse response = orderService.patchOrder(request, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(201, "패치 주문 완료", response));
    }

    // 1:1 커스텀 요청
    @Operation(summary = "1:1 커스텀 요청 주문 API", description = "사용자가 피드 사진, 작가, 부착 위치를 정해 1:1 커스텀 패치를 신청하는 API")
    @PostMapping("/orders/premiums")
    public ResponseEntity<BaseResponse<PremiumOrderResponse>> premiumOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PremiumOrderRequest request) {

        // service 호출
        PremiumOrderResponse response = orderService.premiumOrder(request, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(201, "1:1 커스텀 요청 주문 완료", response));
    }
}
