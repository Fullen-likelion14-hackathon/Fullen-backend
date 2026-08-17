package com.erbe.erbebackend.domain.artist.repository;

import com.erbe.erbebackend.domain.artist.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    // 작가 리스트 조회 시 id 오름차순
    List<Artist> findAllByOrderByIdAsc();
}
