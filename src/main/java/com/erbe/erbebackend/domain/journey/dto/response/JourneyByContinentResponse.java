package com.erbe.erbebackend.domain.journey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@Schema(description = "JourneyByContinentResponse: 대륙별 여행 리스트")
public class JourneyByContinentResponse {

    private Map<String, ContinentJourneyGroupResponse> continents;
}
