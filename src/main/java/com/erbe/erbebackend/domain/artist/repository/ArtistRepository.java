package com.erbe.erbebackend.domain.artist.repository;

import com.erbe.erbebackend.domain.artist.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
