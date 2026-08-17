package com.erbe.erbebackend.domain.patch.exception;

import com.erbe.erbebackend.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PatchErrorCode implements BaseErrorCode {

    PATCH_NOT_FOUND("P4001", "패치를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PATCH_ACCESS_DENIED("P4002", "본인이 소유한 패치가 아닙니다.", HttpStatus.FORBIDDEN),
    PATCH_ALREADY_SAVED("P4003", "이미 저장된 패치입니다.", HttpStatus.CONFLICT),
    PATCH_IN_USE("P4004", "패치가 사용중입니다.", HttpStatus.CONFLICT),
    PATCH_POSITION_NOT_FOUND("P4005", "패치 위치를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PATCH_POSITION_NOT_EDITABLE("P4006", "패치 위치를 수정할 수 없습니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
