package com.erbe.erbebackend.domain.auth.service;

import com.erbe.erbebackend.domain.auth.dto.request.LoginRequest;
import com.erbe.erbebackend.domain.auth.dto.response.LoginResponse;
import com.erbe.erbebackend.domain.auth.exception.AuthErrorCode;
import com.erbe.erbebackend.global.exception.CustomException;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import com.erbe.erbebackend.global.security.CustomUserDetailsService;
import com.erbe.erbebackend.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {


    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 로그인
    public LoginResponse login(LoginRequest request) {

        // email로 사용자 조회
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(request.getEmail());

        // 입력한 비밀번호와 DB에 저장된 암호화 비밀번호 비교
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new CustomException(AuthErrorCode.PASSWORD_FAIL);
        }

        // 로그인 성공 시 Access Token 발급
        String accessToken = jwtProvider.createAccessToken(userDetails);

        // AccessToken을 응답으로 반환
        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
