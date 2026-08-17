package com.erbe.erbebackend.global.s3.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ImageDirectory {

    @Schema(description = "여행 기록 피드 사진")
    FEED,

    @Schema(description = "AI 트래블 패치 생성용 사진")
    TRAVEL_PATCH,

    @Schema(description = "나라별 국기")
    NATION,

    @Schema(description = "작가")
    ARTIST;
}
