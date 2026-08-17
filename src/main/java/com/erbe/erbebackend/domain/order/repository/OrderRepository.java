package com.erbe.erbebackend.domain.order.repository;

import com.erbe.erbebackend.domain.order.entity.Order;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 사용자의 일반 주문(패치+이니셜) 목록 최신순 조회
    List<Order> findAllByUserOrderByIdDesc(User user);
}
