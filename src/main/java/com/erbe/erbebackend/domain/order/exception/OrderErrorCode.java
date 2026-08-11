package com.erbe.erbebackend.domain.order.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {

    NO_PATCH_ATTACH("O4001", "부착된 패치가 없어 주문이 불가능합니다.", HttpStatus.NOT_FOUND),
    PREMIUM_ORDER_NOT_FOUND("O4002", "1:1 커스텀 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PREMIUM_ORDER_ACCESS_DENIED("O4003", "본인이 신청한 1:1 커스텀 주문이 아닙니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
