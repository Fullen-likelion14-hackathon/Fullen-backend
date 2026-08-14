package com.erbe.erbebackend.domain.order.repository;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.order.entity.Initial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InitialRepository extends JpaRepository<Initial, Long> {

    // 주문이 안된 이니셜이 있는지 조회
    List<Initial> findAllByUserBagAndOrderIsNull(UserBag userBag);

    // 가방에 적용된 이니셜 전체 조회
    List<Initial> findAllByUserBag(UserBag userBag);
}
