package com.erbe.erbebackend.domain.artist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "작가 리스트 조회 응답 dto", description = "사용자가 작가 리스트를 조회할때 서버가 반환하는 데이터")
public class ArtistListResponse {

    @Schema(description = "작가 고유번호", example = "1")
    private Long artistId;

    @Schema(description = "작가 이름", example = "빈센트 반 고흐")
    private String artistName;

    @Schema(description = "작가 대표 이미지", example = "https://s3.aws.com/testURL1")
    private String imgUrl;

    @Schema(description = "작가 국적 국기 사진", example = "https://s3.aws.com/testURL1")
    private String nationImgUrl;

    @Schema(description = "작가 소개 한 줄 요약", example = "강렬한 색채와 두꺼운 붓질, 역동적인 곡선으로 감정과 움직임을 표현하는 화풍.")
    private String introSummary;
}
