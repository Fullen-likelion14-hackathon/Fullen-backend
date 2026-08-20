package com.erbe.erbebackend.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "1:1 커스텀 요청후 상세조회 응답 dto", description = "사용자가 1:1 커스텀 요청 주문후 상세조회 할 때 서버가 반환하는 데이터")
public class PremiumOrderSearchResponse {

    @Schema(description = "1:1 커스텀 요청 주문 고유번호", example = "1")
    private Long premiumOrderId;

    @Schema(description = "커스텀 요청한 피드 사진 URL", example = "https://s3.aws.com/photo/travel.png")
    private String photoImgUrl;

    @Schema(description = "선택한 작가 이름", example = "빈센트 반 고흐")
    private String artistName;

    @Schema(description = "선택한 작가 대표 이미지 URL", example = "https://s3.aws.com/artist/vangogh.png")
    private String artistImgUrl;

    @Schema(description = "작가 화풍 한 줄 요약", example = "강렬한 색채와 두꺼운 붓질, 역동적인 곡선으로 감정과 움직임을 표현하는 화풍.")
    private String introSummary;

    @Schema(description = "사진 설명 및 요청사항", example = "이 사진은 독일의 브란덴부르크문 근처 작은 카페에서 찍은 사진입니다")
    private String requestDetail;

    @Schema(description = "2D 사진 상 미리보기 x 좌표", example = "0.5")
    private Double previewX;

    @Schema(description = "2D 사진 상 미리보기 y 좌표", example = "0.5")
    private Double previewY;
}
