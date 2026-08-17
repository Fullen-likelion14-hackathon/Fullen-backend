package com.erbe.erbebackend.domain.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(title = "PhotoDetailResponse: 사진 상세정보 응답 DTO")
public class PhotoDetailResponse {

    @Schema(description = "사진 ID")
    private Long photoId;

    @Schema(description = "사진 URL")
    private String imgURL;

    @Schema(description = "국가", example = "독일")
    private String nationKRName;

    @Schema(description = "여행 타입", example = "여름맞이 가족여행")
    private String journeyType;

    @Schema(description = "사진 일자", example = "2026-08-04")
    private LocalDate date;

}
