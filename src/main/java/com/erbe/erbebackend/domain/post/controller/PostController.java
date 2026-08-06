package com.erbe.erbebackend.domain.post.controller;

import com.erbe.erbebackend.domain.post.dto.request.PostCreateRequest;
import com.erbe.erbebackend.domain.post.dto.response.PostResponse;
import com.erbe.erbebackend.domain.post.service.PostService;
import com.erbe.erbebackend.global.common.BaseResponse;
import com.erbe.erbebackend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Post", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성 API", description = "사용자 요청에 따라 게시글이 작성되는 API입니다.")
    @PostMapping("/journeys/{journeyId}/posts")
    public ResponseEntity<BaseResponse<PostResponse>> createPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long journeyId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostResponse response = postService.createPost(request, journeyId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "게시물 생성 완료", response));
    }

}
