package com.erbe.erbebackend.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PostCardResponse: 게시물 리스트 형식일때의 응답 DTO")
public class PostCardResponse {

    @Schema(description = "게시물 아이디", example = "1")
    private Long postId;

    @Schema(description = "썸네일 이미지 URL", example = "https://s3.aws.com/testURL")
    private String thumbnailURL;

}
