package com.erbe.erbebackend.domain.patch.repository;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.patch.entity.PatchPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatchPositionRepository extends JpaRepository<PatchPosition, Long> {

    // 가방에 적용된 패치 목록 조회
    List<PatchPosition> findAllByUserBag(UserBag userBag);
}
