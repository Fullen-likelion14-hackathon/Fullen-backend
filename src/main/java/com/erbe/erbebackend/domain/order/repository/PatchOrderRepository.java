package com.erbe.erbebackend.domain.order.repository;

import com.erbe.erbebackend.domain.order.entity.PatchOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatchOrderRepository extends JpaRepository<PatchOrder, Long> {
}
