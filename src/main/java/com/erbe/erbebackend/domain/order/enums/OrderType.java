package com.erbe.erbebackend.domain.order.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum OrderType {

    @Schema(description = "패치 + 이니셜 주문")
    REGULAR,

    @Schema(description = "1:1 커스텀 주문")
    PREMIUM
}
