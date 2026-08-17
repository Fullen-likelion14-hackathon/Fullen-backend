package com.erbe.erbebackend.domain.order.dto.request;

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
}
