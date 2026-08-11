package com.erbe.erbebackend.domain.photo.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PhotoErrorCode implements BaseErrorCode {

    PHOTO_NOT_FOUND("PH4001", "사진을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PHOTO_ACCESS_DENIED("PH4002", "본인이 올린 사진이 아닙니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
