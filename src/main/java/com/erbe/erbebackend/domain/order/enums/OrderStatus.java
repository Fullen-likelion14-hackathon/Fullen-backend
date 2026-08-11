package com.erbe.erbebackend.domain.order.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum OrderStatus {

    @Schema(description = "주문 완료")
    ORDER_COMPLETED,

    @Schema(description = "수령 완료(MCM에서 수령")
    RECEIVED,

    @Schema(description = "제작중")
    MAKING,

    @Schema(description = "제작 완료")
    MAKING_COMPLETED,

    @Schema(description = "배송중")
    SHIPPING,

    @Schema(description = "배송 완료")
    DELIVERED
}
