package com.erbe.erbebackend.domain.post.repository;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.domain.post.entity.Post;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Boolean existsByJourney(Journey journey);

    List<Post> findAllByJourneyOrderByCreatedAtAsc(Journey journey);

    List<Post> findByUserNotAndIsPublicAndNationAndCreatedDateGreaterThanEqual(User user, Boolean isPublic, Nation nation, LocalDate createdDateIsGreaterThan);

    List<Post> findByUserNotAndIsPublicAndCreatedDateGreaterThanEqual(User user, Boolean isPublic, LocalDate createdDateIsGreaterThan);

    List<Post> findAllByJourney(Journey journey);
}
