package com.erbe.erbebackend.domain.order.controller;

import com.erbe.erbebackend.domain.order.dto.request.InitialApplyRequest;
import com.erbe.erbebackend.domain.order.dto.request.OrderRequest;
import com.erbe.erbebackend.domain.order.dto.request.PremiumOrderRequest;
import com.erbe.erbebackend.domain.order.dto.response.*;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Order", description = "주문 관련 API")
public class OrderController {

    private final OrderService orderService;

    // 가방에 적용된 패치/이니셜 주문
    @Operation(summary = "주문 API", description = "가방에 적용된 패치, 이니셜을 모아서 주문하는 API (패치 또는 이니셜 중 최소 하나는 있어야 함)")
    @PostMapping("/orders")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OrderRequest request) {

        // service 호출
        OrderResponse response = orderService.createOrder(request, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(201, "주문 완료", response));
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

    // 1:1 커스텀 요청 주문 상세조회
    @Operation(summary = "1:1 커스텀 요청 주문 상세조회 API", description = "사용자가 신청한 1:1 커스텀 주문 상세 내용을 조회하는 API")
    @GetMapping("/orders/premiums/{premium-id}")
    public ResponseEntity<BaseResponse<PremiumOrderSearchResponse>> premiumOrderSearch(
            @PathVariable("premium-id") Long premiumOrderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        PremiumOrderSearchResponse response = orderService.premiumOrderSearch(premiumOrderId, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "1:1 커스텀 요청 상세조회 성공", response));
    }

    // 이니셜 적용
    @Operation(summary = "이니셜 적용 API", description = "사용자가 텍스트/색상/볼드 여부를 정해 가방에 이니셜을 적용하는 API")
    @PostMapping("/orders/initials")
    public ResponseEntity<BaseResponse<InitialApplyResponse>> applyInitial(
            @Valid @RequestBody InitialApplyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        InitialApplyResponse response = orderService.applyInitial(request, userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(201, "이니셜 적용 성공", response));
    }

    // 주문 목록 조회
    @Operation(summary = "주문 목록 최신순 조회 API", description = "사용자가 본인의 주문 목록을 최신순으로 조회하는 API")
    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<List<OrderListResponse>>> listOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // service 호출
        List<OrderListResponse> response = orderService.orderList(userDetails.getId());

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "주문 목록 조회 성공", response));
    }

    // 이니셜 적용 조회
    @Operation(summary = "이니셜 적용 조회 API", description = "사용자 본인 소유 가방에 적용된 이니셜을 조회하는 API")
    @GetMapping("/orders/initials")
    public ResponseEntity<BaseResponse<List<InitialApplyResponse>>> initialList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long userBagId) {

        // service 호출
        List<InitialApplyResponse> response = orderService.initialApplyList(userDetails.getId(), userBagId);

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "이니셜 조회 성공", response));
    }
}
