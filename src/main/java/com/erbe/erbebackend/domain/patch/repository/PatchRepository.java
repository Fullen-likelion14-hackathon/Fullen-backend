package com.erbe.erbebackend.domain.patch.repository;

import com.erbe.erbebackend.domain.patch.entity.Patch;
import com.erbe.erbebackend.domain.patch.enums.PatchType;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatchRepository extends JpaRepository<Patch, Long> {

    // 사용자가 가지고 있는 패치를 티켓, 스탬프, 라벨별로 최신순으로 불러오기
    List<Patch> findAllByUserAndTypeOrderByIdDesc(User user, PatchType type);

    // 같은 유저가 같은 이미지를 이미 저장했는지 확인
    boolean existsByUserAndImgUrl(User user, String imgUrl);

    // 삭제 안 된 패치만 목록 조회
    List<Patch> findAllByUserAndTypeAndIsDeletedFalseOrderByIdDesc(User user, PatchType type);
}
