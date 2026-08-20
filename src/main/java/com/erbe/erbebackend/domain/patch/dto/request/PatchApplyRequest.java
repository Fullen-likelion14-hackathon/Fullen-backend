package com.erbe.erbebackend.domain.patch.dto.request;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.bag.enums.BagSide;
import com.erbe.erbebackend.domain.patch.enums.PatchType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(title = "패치를 가방에 적용하는 요청 dto", description = "사용자가 패치를 가방에 적용할 때 서버에 요청 보내는 데이터")
public class PatchApplyRequest {

    @Schema(description = "가방 고유번호", example = "1")
    @NotNull(message = "가방 고유번호는 필수 입력값입니다.")
    private Long userBagId;

    @Schema(description = "패치 고유번호", example = "1")
    @NotNull(message = "패치 고유번호는 필수 입력값입니다.")
    private Long patchId;

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

    @Schema(description = "패치 크기 배율", example = "0.8")
    @NotNull(message = "크기 배율은 필수 입력값입니다.")
    private Double scale;

    @Schema(description = "좌우 반전 여부", example = "false")
    @NotNull(message = "좌우 반전 여부는 필수 입력값입니다.")
    private Boolean flipped;

    @Schema(description = "겹칠 때 표시 순서 (클수록 위)", example = "1")
    @NotNull(message = "레이어 값은 필수 입력값입니다.")
    private Integer layer;
}
