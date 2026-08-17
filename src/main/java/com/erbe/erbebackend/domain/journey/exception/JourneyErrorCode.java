package com.erbe.erbebackend.domain.journey.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JourneyErrorCode implements BaseErrorCode {
    JOURNEY_NOT_FOUND("J001", "여행을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_JOURNEY_OWNER("J002", "다른 사람의 여행입니다.", HttpStatus.FORBIDDEN),;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
