package com.erbe.erbebackend.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "PostCreateRequest: 게시물 생성 요청 DTO")
public class PostCreateRequest {

    @Schema(description = "본문", example = "브란덴부르크문 근처를 드라이브하다\n 우연히 들른~~")
    private String comment;

    @Schema(description = "공개 여부", example = "true")
    private Boolean isPublic;

    @NotEmpty
    @Schema(description = "이미지 URL 리스트", example = "[\"https://s3.aws.com/testURL1\", \"https://s3.aws.com//testURL2\"]")
    private List<String> imgUrlList;
}
