package com.erbe.erbebackend.infrastructure.openai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "AnalysisResponse: 여행분석 응답 DTO")
public class AnalysisResponse {

    @Schema(description = "여행 분석 결과", example = "여행 분석 결과 ~~~")
    private String answer;

    public static AnalysisResponse of(String answer){
        return new AnalysisResponse(answer);
    }
}
