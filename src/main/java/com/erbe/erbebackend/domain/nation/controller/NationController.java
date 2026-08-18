package com.erbe.erbebackend.domain.nation.controller;

import com.erbe.erbebackend.domain.nation.dto.response.NationResponse;
import com.erbe.erbebackend.domain.nation.service.NationService;
import com.erbe.erbebackend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/nations")
@Tag(name = "Nation", description = "국가 관련 API")
public class NationController {

    private final NationService nationService;

    @GetMapping
    @Operation(summary = "국가 리스트 조회 API", description = "모든 국가 리스트를 조회하는 API")
    public ResponseEntity<BaseResponse<List<NationResponse>>> getAllNations() {
        List<NationResponse> responseList = nationService.getNations();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(200, "국가 리스트 조회에 성공하였습니다.", responseList));
    }
}
