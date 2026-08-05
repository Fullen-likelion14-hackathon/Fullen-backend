package com.erbe.erbebackend.domain.journey.repository;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    Optional<List<Journey>> findAllByUser(User user);
}
