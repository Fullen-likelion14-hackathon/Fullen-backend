package com.erbe.erbebackend.domain.bag.repository;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBagRepository extends JpaRepository<UserBag, Long> {

    // 사용자 소유 가방 목록 조회
    List<UserBag> findAllByUser(User user);
}
