package com.erbe.erbebackend.global.s3.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {

    S3_UPLOAD_FAILED("S5001", "이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_EMPTY_FILE("S4001", "빈 파일은 업로드할 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
