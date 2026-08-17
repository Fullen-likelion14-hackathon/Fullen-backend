package com.erbe.erbebackend.infrastructure.openai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "AnalysisResponse: 여행분석 응답 DTO")
public class AnalysisResponse {

    @Schema(description = "유저 이름", example = "멋쟁이사자처럼")
    private String username;

    @Schema(description = "여행 스타일", example = "Urban Minimalist")
    private String travelStyle;

    @Schema(description = "상세 정보", example = "도시를 좋아하는 ~")
    private String detail;

    @Schema(description = "추천 아티스트 ID 리스트")
    private List<Long> artistIdList;

    @Schema(description = "해쉬태그 리스트")
    private List<String> hashtagList;

    public void setUsername(String username) {
        this.username = username;
    }
}
