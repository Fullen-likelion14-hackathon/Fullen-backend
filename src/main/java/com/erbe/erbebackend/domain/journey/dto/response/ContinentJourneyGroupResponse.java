package com.erbe.erbebackend.domain.journey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "ContinentJourneyGroupResponse : 여행 개수 + JourneyResponse 응답 DTO 리스트를 원소로 가지는 DTO")
public class ContinentJourneyGroupResponse {

    @Schema(description = "여행 개수", example = "5")
    private int count;

    private List<JourneyResponse> journeys;
}
