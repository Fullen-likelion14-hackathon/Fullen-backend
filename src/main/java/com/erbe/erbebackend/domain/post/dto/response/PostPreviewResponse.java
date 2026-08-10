package com.erbe.erbebackend.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(title = "PostPreviewResponse: 게시물 미리보기 응답 DTO")
public class PostPreviewResponse {

    @Schema(description = "게시물 아이디", example = "1")
    private Long postId;

    @Schema(description = "국기 이미지 URL", example = "https://s3.aws.com/testURL1")
    private String nationFlagURL;

    @Schema(description = "한국어 나라 이름", example = "독일")
    private String nationKRName;

    @Schema(description = "여행 타입", example = "여름맞이 가족여행")
    private String journeyType;

    @Schema(description = "포스트 날짜", example = "2026-08-04")
    private LocalDate date;

    @Schema(description = "썸네일 URL", example = "https://s3.aws.com/testURL1")
    private String thumbnailURL;
}
