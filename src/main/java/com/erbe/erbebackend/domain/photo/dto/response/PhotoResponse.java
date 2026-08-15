package com.erbe.erbebackend.domain.photo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PhotoResponse: 사진 응답 DTO")
public class PhotoResponse {

    @Schema(description = "사진 ID")
    private Long photoId;

    @Schema(description = "사진 URL")
    private String imgURL;
}
