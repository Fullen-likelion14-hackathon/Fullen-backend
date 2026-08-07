package com.erbe.erbebackend.domain.artist.repository;

import com.erbe.erbebackend.domain.artist.entity.Artist;
import com.erbe.erbebackend.domain.artist.entity.ArtistImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistImageRepository extends JpaRepository<ArtistImage, Long> {

    // 특정 작가의 그림을 순서 오름차순으로 조회
    List<ArtistImage> findAllByArtistOrderBySeqAsc(Artist artist);
}
