package com.erbe.erbebackend.domain.order.dto.response;

import com.erbe.erbebackend.domain.order.enums.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "주문 리스트 조회 응답 dto", description = "사용자의 전체 주문 내역을 조회할때 서버가 반환하는 데이터")
public class OrderListResponse {

    @Schema(description = "주문 종류 (REGULAR: 패치/이니셜 주문, PREMIUM: 1:1 커스텀 주문)", example = "REGULAR")
    private OrderType type;

    @Schema(description = "주문 고유번호", example = "1")
    private Long orderId;

    @Schema(description = "가방 앞면 사진", example = "https://s3.aws.com/bag/front.png")
    private String frontImgUrl;

    @Schema(description = "주문일시", example = "2026-08-12T17:11:18")
    private LocalDateTime createdAt;
}
