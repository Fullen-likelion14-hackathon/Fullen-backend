package com.erbe.erbebackend.domain.journey.repository;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
}
