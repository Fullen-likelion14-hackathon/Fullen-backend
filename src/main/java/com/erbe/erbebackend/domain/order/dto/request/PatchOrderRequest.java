package com.erbe.erbebackend.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(title = "패치 주문 요청 dto", description = "사용자가 패치를 가방에 적용후 주문을 요청할때 서버에 요청 보내는 데이터")
public class PatchOrderRequest {

    @Schema(description = "가방 고유번호", example = "1")
    @NotNull(message = "가방 고유번호는 필수 입력값입니다.")
    private Long userBagId;
}
