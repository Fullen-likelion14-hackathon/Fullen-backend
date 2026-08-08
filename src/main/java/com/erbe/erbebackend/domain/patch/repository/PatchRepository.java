package com.erbe.erbebackend.domain.patch.repository;

import com.erbe.erbebackend.domain.patch.entity.Patch;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatchRepository extends JpaRepository<Patch, Long> {

    // 사용자가 가지고 있는 패치 최신순으로 불러오기
    List<Patch> findAllByUserOrderByIdDesc(User user);

    // 같은 유저가 같은 이미지를 이미 저장했는지 확인
    boolean existsByUserAndImgUrl(User user, String imgUrl);
}
