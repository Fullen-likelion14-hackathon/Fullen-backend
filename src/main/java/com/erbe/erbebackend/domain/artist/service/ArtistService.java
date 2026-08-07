package com.erbe.erbebackend.domain.artist.service;

import com.erbe.erbebackend.domain.artist.dto.response.ArtistListResponse;
import com.erbe.erbebackend.domain.artist.dto.response.ArtistSearchResponse;
import com.erbe.erbebackend.domain.artist.entity.Artist;
import com.erbe.erbebackend.domain.artist.entity.ArtistImage;
import com.erbe.erbebackend.domain.artist.exception.ArtistErrorCode;
import com.erbe.erbebackend.domain.artist.repository.ArtistImageRepository;
import com.erbe.erbebackend.domain.artist.repository.ArtistRepository;
import com.erbe.erbebackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ArtistService {

    private final ArtistImageRepository artistImageRepository;
    private final ArtistRepository artistRepository;

    // 작가 상세조회
    public ArtistSearchResponse artistSearch(Long artistId) {

        // 작가가 존재하는지 조회
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ArtistErrorCode.ARTIST_NOT_FOUND));

        // 사진 가져오기
        List<ArtistImage> images = artistImageRepository.findAllByArtistOrderBySeqAsc(artist);
        List<String> imgUrls = new ArrayList<>();
        for (ArtistImage image : images) {
            imgUrls.add(image.getImgUrl());
        }

        // 작가 국적 사진
        String nationImgUrl = artist.getNation().getImgUrl();

        // 로그 출력
        log.info("[ArtistService] 작가 상세보기 조회 성공: artistId={}", artistId);

        // 응답 세팅
        return ArtistSearchResponse.builder()
                .artistId(artistId)
                .artistName(artist.getName())
                .imgUrls(imgUrls)
                .introSummary(artist.getIntroSummary())
                .description(artist.getDescription())
                .nationImgUrl(nationImgUrl)
                .build();
    }

    // 작가 리스트 조회
    public List<ArtistListResponse> artistList() {

        // 응답 세팅
        List<ArtistListResponse> list = new ArrayList<>();
        for (Artist artist : artistRepository.findAll()) {
            list.add(ArtistListResponse.builder()
                    .artistId(artist.getId())
                    .artistName(artist.getName())
                    .imgUrl(artist.getImgUrl())
                    .build());
        }

        // 로그 출력
        log.info("[ArtistService] 작가 리스트 조회 성공");

        return list;
    }
}
