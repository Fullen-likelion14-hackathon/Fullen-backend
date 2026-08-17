package com.erbe.erbebackend.domain.patch.dto.request;

import com.erbe.erbebackend.domain.patch.enums.PatchType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(title = "패치 저장 요청 dto", description = "사용자가 패치를 저장할 때 서버에 요청 보내는 데이터")
public class PatchSaveRequest {

    @Schema(description = "패치 종류", example = "STAMP")
    @NotNull(message = "패치 종류는 필수 입력값입니다.")
    private PatchType type;

    @Schema(description = "AI가 만들어준 패치 사진 URL", example = "https://s3.aws.com/patch/xxx.png")
    @NotBlank(message = "사진 URL은 필수 입력값입니다.")
    private String imgUrl;
}
