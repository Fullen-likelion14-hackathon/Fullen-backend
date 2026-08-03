package com.erbe.erbebackend.global.exception.model;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    String getCode();

    String getMessage();

    HttpStatus getStatus();
}
