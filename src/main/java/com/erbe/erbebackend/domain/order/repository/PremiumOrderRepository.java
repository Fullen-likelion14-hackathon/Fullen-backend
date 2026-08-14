package com.erbe.erbebackend.domain.order.repository;

import com.erbe.erbebackend.domain.order.entity.PremiumOrder;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PremiumOrderRepository extends JpaRepository<PremiumOrder, Long> {

    // 사용자의 1:1 커스텀 주문 목록 최신순 조회
    List<PremiumOrder> findAllByUserOrderByIdDesc(User user);
}
