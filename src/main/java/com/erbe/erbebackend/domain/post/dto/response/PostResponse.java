package com.erbe.erbebackend.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Schema(title = "PostResponse: 게시물 응답 DTO")
public class PostResponse {

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

    @Schema(description = "포스트 이미지 URL 리스트", example = "[\"https://s3.aws.com/testURL1\", \"https://s3.aws.com//testURL2\"]")
    private List<String> imgUrlList;

    @Schema(description = "본문", example = "브란덴부르크문 근처를 드라이브하다\n 우연히 들른~~")
    private String comment;

    @Schema(description = "사진 개수", example = "5")
    private int photoCount;

    @Schema(description = "본문 글자 개수", example = "52")
    private int commentLength;

    @Schema(description = "공개 여부", example = "true")
    private Boolean isPublic;
}
