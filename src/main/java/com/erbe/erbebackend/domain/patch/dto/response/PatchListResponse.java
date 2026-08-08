package com.erbe.erbebackend.domain.patch.dto.response;

import com.erbe.erbebackend.domain.patch.enums.PatchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "패치 리스트 조회 응답 dto", description = "사용자가 패치 리스트를 조회할때 서버가 반환하는 데이터")
public class PatchListResponse {

    @Schema(description = "패치 고유번호", example = "1")
    private Long patchId;

    @Schema(description = "패치 종류", example = "STAMP")
    private PatchType type;

    @Schema(description = "AI가 생성해준 패치 사진 URL", example = "https://s3.aws.com/patch/xxx.png")
    private String imgUrl;
}
