package com.erbe.erbebackend.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(title = "주문 요청 dto", description = "사용자가 패치 및 이니셜을 가방에 적용후 주문을 요청할때 서버에 요청 보내는 데이터")
public class OrderRequest {

    @Schema(description = "가방 고유번호", example = "1")
    @NotNull(message = "가방 고유번호는 필수 입력값입니다.")
    private Long userBagId;

    @Schema(description = "커스텀 적용된 가방 앞면 캡처 이미지 URL", example = "https://s3.aws.com/images/custom_front.png")
    @NotBlank(message = "커스텀 앞면 이미지는 필수 입력값입니다.")
    private String customFrontImgUrl;

    @Schema(description = "커스텀 적용된 가방 뒷면 캡처 이미지 URL", example = "https://s3.aws.com/images/custom_back.png")
    @NotBlank(message = "커스텀 뒷면 이미지는 필수 입력값입니다.")
    private String customBackImgUrl;
}
