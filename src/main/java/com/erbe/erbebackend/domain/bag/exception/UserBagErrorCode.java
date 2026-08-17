package com.erbe.erbebackend.domain.bag.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserBagErrorCode implements BaseErrorCode {

    USER_BAG_NOT_FOUND("B4001", "가방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_BAG_ACCESS_DENIED("B4002", "본인 소유의 가방이 아닙니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
