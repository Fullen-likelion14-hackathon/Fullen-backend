package com.erbe.erbebackend.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title = "LoginRequest: 로그인 요청 DTO", description = "이메일과 비밀번호를 받아 로그인하는 요청 Request")
public class LoginRequest {

    @NotBlank(message = "사용자 이메일 항목은 필수입니다.")
    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @NotBlank(message = "사용자 비밀번호 항목은 필수입니다.")
    @Schema(description = "비밀번호", example = "password123!")
    private String password;
}
