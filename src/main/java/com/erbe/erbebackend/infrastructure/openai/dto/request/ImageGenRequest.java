package com.erbe.erbebackend.infrastructure.openai.dto.request;

import com.erbe.erbebackend.infrastructure.openai.enums.TravelPatchType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ImageGenRequest: 트래블패치 생성 요청 dto")
public class ImageGenRequest {

    @NotNull
    @Schema(description = "사진 ID", example = "1")
    private Long photoId;

    @NotNull
    @Schema(description = "요청 메시지(여행 분석 결과)", example = "여행 스타일은 ~~")
    private String message;

    @NotNull
    @Schema(description = "트래블패치 유형", example = "LABEL")
    private TravelPatchType type;
}
