package com.erbe.erbebackend.domain.order.dto.request;

import com.erbe.erbebackend.domain.bag.enums.BagSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(title = "1:1 커스텀 주문 요청 dto", description = "사용자가 1:1 커스텀 요청 주문을 요청할때 서버에 요청 보내는 데이터")
public class PremiumOrderRequest {

    @Schema(description = "가방 고유번호", example = "1")
    @NotNull(message = "가방 고유번호는 필수 입력값입니다.")
    private Long userBagId;

    @Schema(description = "커스텀할 사진 고유번호", example = "1")
    @NotNull(message = "사진은 필수 입력값입니다.")
    private Long photoId;

    @Schema(description = "작가 고유번호", example = "1")
    @NotNull(message = "작가는 필수 입력값입니다.")
    private Long artistId;

    @Schema(description = "사진 설명 및 요청사항", example = "여행한 나라의 이름이 패치에서 가장 잘 보이도록 크게 표현되었으면 좋겠습니다.")
    @NotBlank(message = "요청사항은 필수 입력값입니다.")
    @Size(max = 3000, message = "요청사항은 3000자 이하여야 합니다.")
    private String requestDetail;

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

    @Schema(description = "2D 사진 미리보기 x 좌표", example = "0.5")
    @NotNull(message = "미리보기 x 좌표는 필수 입력값입니다.")
    private Double previewX;

    @Schema(description = "2D 사진 미리보기 y 좌표", example = "0.5")
    @NotNull(message = "미리보기 y 좌표는 필수 입력값입니다.")
    private Double previewY;
}
