package com.erbe.erbebackend.domain.journey.repository;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.nation.enums.Continent;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    Optional<List<Journey>> findAllByUser(User user);

    List<Journey> findAllByUserOrderByLongitudeAscIdAsc(User user);

    @Modifying
    @Query("UPDATE Journey j SET j.postCount = j.postCount + 1 WHERE j.id = :id")
    void incrementPostCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Journey j SET j.postCount = j.postCount - 1 WHERE j.id = :id")
    void decrementPostCount(@Param("id") Long id);

    Journey findTopByUserAndNationContinentOrderByStartDateDesc(User user, Continent nationContinent);

    List<Journey> findByUserAndNationContinentOrderByStartDateDesc(User user, Continent continent);

    Optional<Journey> findTopByUserOrderByStartDateDesc(User user);
}
