package com.erbe.erbebackend.domain.journey.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(title = "JourneyResponse: 여행 정보 응답 DTO")
public class JourneyResponse {

    @Schema(description = "여행 ID", example = "1")
    private Long journeyId;

    @Schema(description = "국가 이름(한국어)", example = "독일")
    private String nationKRName;

    @Schema(description = "국가 이름(영어)", example = "GERMANY")
    private String nationENName;

    @Schema(description = "여행 타입", example = "여름맞이 가족 여행")
    private String type;

    @Schema(description = "여행 커버 이미지 URL", example = "https://s3.aws.com/coverImgUrl")
    private String coverImgUrl;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @Schema(description = "여행 시작일", example = "2026.07.31")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @Schema(description = "여행 종료일", example = "2026.08.05")
    private LocalDate endDate;

    @Schema(description = "국기 이미지 URL", example = "https://s3.aws.com/testURL1")
    private String flagImgUrl;

    @Schema(description = "게시물 개수", example = "3")
    private int postCount;
}
