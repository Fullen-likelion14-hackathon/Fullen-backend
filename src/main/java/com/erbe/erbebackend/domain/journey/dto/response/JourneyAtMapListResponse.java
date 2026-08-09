package com.erbe.erbebackend.domain.journey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "레전드")
public class JourneyAtMapListResponse {

    private JourneyAtMapResponse leftJourney;

    private JourneyAtMapResponse centerJourney;

    private JourneyAtMapResponse rightJourney;
}
