package com.erbe.erbebackend.domain.patch.repository;

import com.erbe.erbebackend.domain.order.entity.PatchOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatchRepository extends JpaRepository<PatchOrder, Long> {
}
