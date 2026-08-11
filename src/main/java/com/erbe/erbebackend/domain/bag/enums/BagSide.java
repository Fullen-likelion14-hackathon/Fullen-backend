package com.erbe.erbebackend.domain.bag.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum BagSide {

    @Schema(description = "가방 앞면")
    FRONT,

    @Schema(description = "가방 뒷면")
    BACK
}
