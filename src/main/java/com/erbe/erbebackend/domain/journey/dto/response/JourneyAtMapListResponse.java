package com.erbe.erbebackend.domain.journey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "JourneyAtMapListResponse: 지도 화면 내에서, 센터 + 좌/우 여행 정보 응답하는 DTO")
public class JourneyAtMapListResponse {

    private JourneyAtMapResponse leftJourney;

    private JourneyAtMapResponse centerJourney;

    private JourneyAtMapResponse rightJourney;
}
