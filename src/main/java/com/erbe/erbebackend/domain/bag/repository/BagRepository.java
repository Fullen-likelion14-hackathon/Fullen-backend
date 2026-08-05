package com.erbe.erbebackend.domain.bag.repository;

import com.erbe.erbebackend.domain.bag.entity.Bag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BagRepository extends JpaRepository<Bag, Long> {
}
