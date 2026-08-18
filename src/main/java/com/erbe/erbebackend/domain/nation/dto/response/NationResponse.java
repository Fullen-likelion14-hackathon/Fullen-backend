package com.erbe.erbebackend.domain.nation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "NationResponse : 국가 정보 반환 DTO")
public class NationResponse {

    @Schema(description = "국가 한국어 이름", example = "독일")
    private String nationKRName;

    @Schema(description = "국가 영어 이름", example = "GERMANY")
    private String nationENName;
}
