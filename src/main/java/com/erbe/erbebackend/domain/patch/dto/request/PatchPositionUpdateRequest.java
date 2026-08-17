package com.erbe.erbebackend.domain.patch.dto.request;

import com.erbe.erbebackend.domain.bag.enums.BagSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(title = "가방에 부착된 패치 위치 수정 요청 dto", description = "사용자가 가방에 부착된 패치 위치를 수정할 때 서버에 요청 보내는 데이터")
public class PatchPositionUpdateRequest {

    @Schema(description = "가방 면 (앞면/뒷면)", example = "BACK")
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
}
