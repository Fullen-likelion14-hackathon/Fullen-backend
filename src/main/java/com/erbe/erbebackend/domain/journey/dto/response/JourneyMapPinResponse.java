package com.erbe.erbebackend.domain.journey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "JourneyMapPinResponse: 지도 핀 응답 DTO")
public class JourneyMapPinResponse {

    @Schema(description = "여행 ID", example = "1")
    private Long journeyId;

    @Schema(description = "위도")
    private Double latitude;

    @Schema(description = "경도")
    private Double longitude;
}
