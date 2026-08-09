package com.erbe.erbebackend.domain.journey.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(title = "JourneyAtMapResponse: 지도 내에서의 여행 정보 응답 DTO")
public class JourneyAtMapResponse {

    @Schema(description = "여행 ID", example = "1")
    private Long journeyId;

    @Schema(description = "국가 이름(한국어)", example = "독일")
    private String nationKRName;

    @Schema(description = "여행 타입", example = "여름맞이 가족 여행")
    private String type;

    @Schema(description = "여행 썸네일 이미지 URL", example = "https://s3.aws.com/thumbnailUrl")
    private String thumbnailUrl;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @Schema(description = "여행 시작일", example = "2026.07.31")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @Schema(description = "여행 종료일", example = "2026.08.05")
    private LocalDate endDate;

    @Schema(description = "게시물 개수", example = "10")
    private int postCount;

    @Schema(description = "위도")
    private Double latitude;

    @Schema(description = "경도")
    private Double longitude;

    @Schema(description = "국기 이미지 URL", example = "https://s3.aws.com/testURL1")
    private String flagImgUrl;
}
