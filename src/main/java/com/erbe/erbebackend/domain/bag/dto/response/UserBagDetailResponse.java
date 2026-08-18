package com.erbe.erbebackend.domain.bag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "가방 상세 조회 응답 dto", description = "사용자가 소유한 가방 하나를 상세 조회할때 서버가 반환하는 데이터")
public class UserBagDetailResponse {

    @Schema(description = "가방 고유번호", example = "1")
    private Long userBagId;

    @Schema(description = "가방 이름", example = "Ottomar 비세토스 위켄더")
    private String bagName;

    @Schema(description = "가방 사이즈", example = "50.5 cm")
    private String bagSize;

    @Schema(description = "가방 앞면 사진", example = "https://s3.aws.com/bag/front.png")
    private String bagFrontImgUrl;

    @Schema(description = "가방 뒷면 사진", example = "https://s3.aws.com/bag/back.png")
    private String bagBackImgUrl;

    @Schema(description = "시리얼 번호", example = "SN-TEST-0001")
    private String serialNumber;

    @Schema(description = "등록일", example = "2026-08-18")
    private LocalDate useStartDate;
}
