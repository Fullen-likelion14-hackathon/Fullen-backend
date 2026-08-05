package com.erbe.erbebackend.domain.bag.repository;

import com.erbe.erbebackend.domain.bag.entity.BagProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BagProductRepository extends JpaRepository<BagProduct, Long> {
}
