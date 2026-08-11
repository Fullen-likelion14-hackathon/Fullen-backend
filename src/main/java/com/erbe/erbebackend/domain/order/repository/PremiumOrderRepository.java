package com.erbe.erbebackend.domain.order.repository;

import com.erbe.erbebackend.domain.order.entity.PremiumOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumOrderRepository extends JpaRepository<PremiumOrder, Long> {
}
