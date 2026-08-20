package com.erbe.erbebackend.domain.order.dto.request;

import com.erbe.erbebackend.domain.bag.enums.BagSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(title = "이니셜 적용 수정 요청 dto", description = "사용자가 가방에 이니셜 문구를 넣은것을 수정할때 서버에 요청 보내는 데이터")
public class InitialUpdateRequest {

    @Schema(description = "색상", example = "#ff33cc")
    @NotBlank(message = "색상은 필수 입력값입니다.")
    private String color;

    @Schema(description = "볼드 여부(true는 굵게, false는 기본)", example = "true")
    @NotNull(message = "볼드 여부는 필수 입력값입니다.")
    private Boolean isBold;

    @Schema(description = "가방 면 (앞면/뒷면)", example = "FRONT")
    @NotNull(message = "가방 면은 필수 입력값입니다.")
    private BagSide side;

    @Schema(description = "x 좌표", example = "1.1")
    @NotNull(message = "x 좌표는 필수 입력값입니다.")
    private Double posX;

    @Schema(description = "y 좌표", example = "1.1")
    @NotNull(message = "y 좌표는 필수 입력값입니다.")
    private Double posY;

    @Schema(description = "회전 각도", example = "0.0")
    @NotNull(message = "회전 각도는 필수 입력값입니다.")
    private Double rotation;

    @Schema(description = "이니셜 크기 배율", example = "0.8")
    @NotNull(message = "크기 배율은 필수 입력값입니다.")
    private Double scale;

    @Schema(description = "겹칠 때 표시 순서 (클수록 위)", example = "1")
    @NotNull(message = "레이어 값은 필수 입력값입니다.")
    private Integer layer;
}
