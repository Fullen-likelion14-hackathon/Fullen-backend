package com.erbe.erbebackend.domain.artist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "작가 단건 조회 응답 dto", description = "사용자가 작가 한 명 조회할때 서버가 반환하는 데이터")
public class ArtistSearchResponse {

    @Schema(description = "작가 고유번호", example = "1")
    private Long artistId;

    @Schema(description = "작가 이름", example = "빈센트 반 고흐")
    private String artistName;

    @Schema(description = "작가 이미지 목록 (seq 오름차순)", example = "[\"https://s3.aws.com/testURL1\", \"https://s3.aws.com/testURL2\"]")
    private List<String> imgUrls;

    @Schema(description = "작가 소개 한 줄 요약", example = "강렬한 색채와 두꺼운 붓질, 역동적인 곡선으로 감정과 움직임을 표현하는 화풍.")
    private String introSummary;

    @Schema(description = "작가 소개 전체 본문", example = "빈센트 반 고흐는 네덜란드 출신의 후기 인상주의 화가로...")
    private String description;

    @Schema(description = "작가 국적 국기 사진", example = "https://s3.aws.com/testURL1")
    private String nationImgUrl;
}
