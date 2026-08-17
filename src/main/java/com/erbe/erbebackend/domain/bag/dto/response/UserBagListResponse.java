package com.erbe.erbebackend.domain.bag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "가방 리스트 조회 응답 dto", description = "사용자가 소유한 가방 목록을 조회할때 서버가 반환하는 데이터")
public class UserBagListResponse {

    @Schema(description = "가방 고유번호", example = "1")
    private Long userBagId;

    @Schema(description = "가방 이름", example = "Ottomar 비세토스 위켄더")
    private String bagName;

    @Schema(description = "가방 앞면 사진", example = "https://s3.aws.com/bag/front.png")
    private String bagFrontImgUrl;
}
