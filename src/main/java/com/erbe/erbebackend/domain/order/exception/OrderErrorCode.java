package com.erbe.erbebackend.domain.order.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {

    NOTHING_ORDER("O4001", "주문할 패치나 이니셜이 없습니다.", HttpStatus.CONFLICT),
    PREMIUM_ORDER_NOT_FOUND("O4002", "1:1 커스텀 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PREMIUM_ORDER_ACCESS_DENIED("O4003", "본인이 신청한 1:1 커스텀 주문이 아닙니다.", HttpStatus.FORBIDDEN),
    INITIAL_NOT_FOUND("O4006", "이니셜을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INITIAL_NOT_EDITABLE("O4007", "이미 주문된 이니셜은 수정/삭제할 수 없습니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
