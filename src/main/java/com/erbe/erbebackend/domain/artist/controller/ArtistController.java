package com.erbe.erbebackend.domain.artist.controller;

import com.erbe.erbebackend.domain.artist.dto.response.ArtistListResponse;
import com.erbe.erbebackend.domain.artist.dto.response.ArtistSearchResponse;
import com.erbe.erbebackend.domain.artist.service.ArtistService;
import com.erbe.erbebackend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Artist", description = "작가 관련 API")
public class ArtistController {

    private final ArtistService artistService;

    // 작가 상세보기 조회
    @Operation(summary = "작가 단건 조회 API", description = "사용자가 작가 상세보기 조회할때 사용하는 API")
    @GetMapping("/artists/{artist-id}")
    public ResponseEntity<BaseResponse<ArtistSearchResponse>> artistSearch(
            @PathVariable("artist-id") Long artistId) {

        // service 호출
        ArtistSearchResponse response = artistService.artistSearch(artistId);

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "작가 상세보기 성공", response));
    }

    // 작가 리스트 조회
    @Operation(summary = "작가 리스트 조회 API", description = "사용자가 작가 리스트를 조회할때 사용하는 API")
    @GetMapping("/artists")
    public ResponseEntity<BaseResponse<List<ArtistListResponse>>> artistList() {

        // service 호출
        List<ArtistListResponse> response = artistService.artistList();

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(200, "작가 리스트 조회 성공", response));
    }
}
