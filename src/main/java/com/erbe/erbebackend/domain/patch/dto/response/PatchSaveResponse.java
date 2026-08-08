package com.erbe.erbebackend.domain.patch.dto.response;

import com.erbe.erbebackend.domain.patch.enums.PatchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "패치 등록 응답 dto", description = "사용자가 패치를 등록할때 서버가 반환하는 데이터")
public class PatchSaveResponse {

    @Schema(description = "저장된 패치 고유번호", example = "1")
    private Long patchId;

    @Schema(description = "패치 종류", example = "STAMP")
    private PatchType type;

    @Schema(description = "AI가 생성해준 패치 사진 URL", example = "https://s3.aws.com/patch/xxx.png")
    private String imgUrl;
}
