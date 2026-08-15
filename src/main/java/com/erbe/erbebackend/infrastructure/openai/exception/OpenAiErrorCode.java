package com.erbe.erbebackend.infrastructure.openai.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OpenAiErrorCode implements BaseErrorCode {

    OPENAI_IMAGE_GEN_FAILED("O5001", "트래블 패치 제작에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
