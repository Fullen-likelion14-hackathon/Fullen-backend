package com.erbe.erbebackend.domain.patch.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "패치 적용 응답 dto", description = "사용자가 패치를 가방에 적용할때 서버가 반환하는 데이터")
public class PatchApplyResponse {

    @Schema(description = "패치 부착 위치 고유번호", example = "1")
    private Long patchPositionId;

    @Schema(description = "가방 고유번호", example = "1")
    private Long userBagId;

    @Schema(description = "패치 고유번호", example = "1")
    private Long patchId;

    @Schema(description = "패치 이미지 URL", example = "https://s3.aws.com/patch/xxx.png")
    private String imgUrl;

    @Schema(description = "x 좌표", example = "1.1")
    private Double posX;

    @Schema(description = "y 좌표", example = "1.1")
    private Double posY;

    @Schema(description = "회전 각도", example = "0.0")
    private Double rotation;

    @Schema(description = "수정 여부", example = "true")
    private Boolean isEditable;
}
