package com.erbe.erbebackend.domain.order.dto.response;

import com.erbe.erbebackend.domain.bag.enums.BagSide;
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
@Schema(title = "1:1 커스텀 요청 응답 dto", description = "사용자가 1:1 커스텀 요청 주문후 서버가 반환하는 데이터")
public class PremiumOrderResponse {

    @Schema(description = "프리미엄 주문 고유번호", example = "1")
    private Long premiumOrderId;

    @Schema(description = "가방 고유번호", example = "1")
    private Long userBagId;

    @Schema(description = "선택한 피드 사진 고유번호", example = "1")
    private Long photoId;

    @Schema(description = "선택한 작가 고유번호", example = "1")
    private Long artistId;

    @Schema(description = "사진 설명 및 요청사항", example = "여행한 나라의 이름이 패치에서 가장 잘 보이도록...")
    private String requestDetail;

    @Schema(description = "가방 면 (앞면/뒷면)", example = "FRONT")
    private BagSide side;

    @Schema(description = "x 좌표", example = "1.1")
    private Double posX;

    @Schema(description = "y 좌표", example = "1.1")
    private Double posY;

    @Schema(description = "회전 각도", example = "0.0")
    private Double rotation;

    @Schema(description = "주문 상태", example = "ORDER_COMPLETED")
    private OrderStatus orderStatus;

    @Schema(description = "2D 사진 상 미리보기 x 좌표", example = "0.5")
    private Double previewX;

    @Schema(description = "2D 사진 상 미리보기 y 좌표", example = "0.5")
    private Double previewY;
}
