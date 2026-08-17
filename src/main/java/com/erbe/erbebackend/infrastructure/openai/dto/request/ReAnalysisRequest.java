package com.erbe.erbebackend.infrastructure.openai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ReAnalysisRequest: 여행 분석 재요청 dto")
public class ReAnalysisRequest {

    @NotBlank
    @Schema(description = "사용자 입력 여행 스타일")
    private String request;
}
