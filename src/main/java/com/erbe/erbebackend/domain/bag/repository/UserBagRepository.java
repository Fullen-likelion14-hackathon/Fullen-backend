package com.erbe.erbebackend.domain.bag.repository;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBagRepository extends JpaRepository<UserBag, Long> {
}
