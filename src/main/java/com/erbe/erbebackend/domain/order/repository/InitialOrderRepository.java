package com.erbe.erbebackend.domain.order.repository;

import com.erbe.erbebackend.domain.order.entity.InitialOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitialOrderRepository extends JpaRepository<InitialOrder, Long> {
}
