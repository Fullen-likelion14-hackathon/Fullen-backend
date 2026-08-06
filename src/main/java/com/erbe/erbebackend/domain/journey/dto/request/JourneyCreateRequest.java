package com.erbe.erbebackend.domain.journey.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "JourneyCreateRequest: 여행 생성 요청 DTO")
public class JourneyCreateRequest {

    @Schema(description = "여행 대표 이미지 URL", example = "https://s3.aws.com/testURL")
    private String imgUrl;

    // TODO : 회의할때, nationCode(ex. JP, DE)로 넘겨주는거 물어보기 -> 나라 조회 API 대신, 프론트 소스코드에 DB기반으로 미리 데이터 삽입해두는 방법
    @Schema(description = "국가 이름", example = "독일")
    private String nationName;

    @Schema(description = "여행 유형", example = "우정 여행")
    private String type;

    @Schema(description = "시작 일시", example = "2026-07-07")
    private LocalDate startDate;

    @Schema(description = "종료 일시", example = "2026-08-30")
    private LocalDate endDate;

}
