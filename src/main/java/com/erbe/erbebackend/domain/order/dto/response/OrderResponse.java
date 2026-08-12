package com.erbe.erbebackend.domain.order.dto.response;

import com.erbe.erbebackend.domain.order.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "주문 응답 dto", description = "사용자가 가방에 달린 이니셜 및 패치를 주문후 서버가 반환하는 데이터")
public class OrderResponse {

    @Schema(description = "주문 고유번호", example = "1")
    private Long orderId;

    @Schema(description = "주문한 가방 고유번호", example = "1")
    private Long userBagId;

    @Schema(description = "주문 상태", example = "DELIVERED")
    private OrderStatus orderStatus;

    @Schema(description = "가방 이름", example = "Ottomar 비세토스 위켄더")
    private String bagName;

    @Schema(description = "가방 사이즈", example = "50.5 cm (19.9 in)")
    private String bagSize;

    @Schema(description = "가방 앞면 사진", example = "https://s3.aws.com/bag/front.png")
    private String bagFrontImgUrl;

    @Schema(description = "가방 뒷면 사진", example = "https://s3.aws.com/bag/back.png")
    private String bagBackImgUrl;
}
