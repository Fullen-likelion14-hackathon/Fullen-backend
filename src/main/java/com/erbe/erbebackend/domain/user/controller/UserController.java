package com.erbe.erbebackend.domain.user.controller;

import com.erbe.erbebackend.domain.user.service.UserService;
import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 닉네임 조회 API", description = "로그인 된 사용자의 닉네임을 반환하는 API")
    @GetMapping("/users/username")
    public ResponseEntity<BaseResponse<String>> getUsername(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        String response = userService.getUserName(customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "유저이름 조회 성공", response));
    }
}
