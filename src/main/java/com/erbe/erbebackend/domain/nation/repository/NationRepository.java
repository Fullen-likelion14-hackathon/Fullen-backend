package com.erbe.erbebackend.domain.nation.repository;

import com.erbe.erbebackend.domain.nation.entity.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NationRepository extends JpaRepository<Nation, Long> {
    Optional<Nation> findByKrName(String krName);

    Optional<Nation> findByEnName(String enName);
}
