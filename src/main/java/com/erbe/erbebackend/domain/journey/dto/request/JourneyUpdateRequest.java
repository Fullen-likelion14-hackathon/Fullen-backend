package com.erbe.erbebackend.domain.journey.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "JourneyUpdateRequest: 여행 수정 요청 DTO")
public class JourneyUpdateRequest {

    @NotBlank
    @Schema(description = "여행 대표 이미지 URL", example = "https://s3.aws.com/testURL")
    private String imgUrl;

    @Schema(description = "여행 유형", example = "우정 여행")
    private String type;

    @NotNull
    @Schema(description = "시작 일시", example = "2026-07-07")
    private LocalDate startDate;

    @NotNull
    @Schema(description = "종료 일시", example = "2026-08-30")
    private LocalDate endDate;

}
