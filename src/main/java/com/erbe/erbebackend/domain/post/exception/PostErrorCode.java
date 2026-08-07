package com.erbe.erbebackend.domain.post.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements BaseErrorCode {
    POST_NOT_FOUND("P001", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_POST_OWNER("P002", "게시글을 조회할 권한이 없습니다.", HttpStatus.FORBIDDEN),;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
