package com.erbe.erbebackend.domain.order.dto.response;

import com.erbe.erbebackend.domain.bag.enums.BagSide;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "이니셜 적용 응답 dto", description = "사용자가 이니셜을 가방에 적용할때 서버가 반환하는 데이터")
public class InitialApplyResponse {

    @Schema(description = "이니셜 주문 고유번호", example = "1")
    private Long initialId;

    @Schema(description = "가방 고유번호", example = "1")
    private Long userBagId;

    @Schema(description = "이니셜 텍스트", example = "MCM")
    private String initialPhrase;

    @Schema(description = "색상", example = "#ff33cc")
    private String color;

    @Schema(description = "볼드 여부(true는 굵게, false는 기본)", example = "true")
    private Boolean isBold;

    @Schema(description = "가방 면 (앞면/뒷면)", example = "FRONT")
    private BagSide side;

    @Schema(description = "x 좌표", example = "1.1")
    private Double posX;

    @Schema(description = "y 좌표", example = "1.1")
    private Double posY;

    @Schema(description = "회전 각도", example = "0.0")
    private Double rotation;

    @Schema(description = "이니셜 크기 배율", example = "0.8")
    private Double scale;
}
