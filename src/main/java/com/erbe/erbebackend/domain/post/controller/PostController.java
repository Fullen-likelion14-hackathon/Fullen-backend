package com.erbe.erbebackend.domain.post.controller;

import com.erbe.erbebackend.domain.post.dto.request.PostCreateRequest;
import com.erbe.erbebackend.domain.post.dto.request.PostUpdateRequest;
import com.erbe.erbebackend.domain.post.dto.response.PostCardResponse;
import com.erbe.erbebackend.domain.post.dto.response.PostPreviewResponse;
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

import java.util.List;

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

    @Operation(summary = "게시글 조회 API", description = "게시글 ID 기반하여 조회하는 API입니다.")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<BaseResponse<PostResponse>> getPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId
    ){
        PostResponse response = postService.getPost(postId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "게시물 조회 완료", response));
    }

    @Operation(summary = "여행 별 게시글 리스트 조회 API", description = "여행 ID 기반하여 게시글 리스트 조회하는 API입니다")
    @GetMapping("/journeys/{journeyId}/posts")
    public ResponseEntity<BaseResponse<List<PostCardResponse>>> getPosts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long journeyId
    ){
        List<PostCardResponse> responseList = postService.getPostListWithJourney(journeyId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "여행의 게시물 리스트 조회 완료", responseList));

    }

    @Operation(summary = "게시물 아카이브 조회 API", description = "타 사용자의 공개 게시물 리스트를 조회하는 API")
    @GetMapping("/posts/archive")
    public ResponseEntity<BaseResponse<List<PostCardResponse>>> getPostsArchive(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String scope
    ){
        List<PostCardResponse> responseList = postService.getPostsArchive(scope, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "게시물 아카이브 조회 완료", responseList));
    }

    @Operation(summary = "게시물 미리보기 API", description = "게시물 아카이브 내에서, 타 사용자 게시물 조회 시 중간 미리보기 반환하는 API")
    @GetMapping("/posts/{postId}/preview")
    public ResponseEntity<BaseResponse<PostPreviewResponse>> getPostPreview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId
    ){
        PostPreviewResponse response = postService.getPostPreview(postId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "게시물 미리보기 조회 완료", response));
    }

    @Operation(summary = "게시물 수정 API", description = "게시물 수정하는 API")
    @PutMapping("/posts/{postId}")
    public ResponseEntity<BaseResponse<PostResponse>> updatePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody PostUpdateRequest request,
            @PathVariable Long postId
    ){
        PostResponse response = postService.updatePost(request, postId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "게시물 수정 성공", response));
    }

    @Operation(summary = "게시물 삭제 API", description = "게시물 삭제하는 API")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<BaseResponse<String>> deletePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId
    ){
        String response = postService.deletePost(postId, customUserDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "게시물 삭제 성공", response));
    }

}
