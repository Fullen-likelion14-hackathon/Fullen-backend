package com.erbe.erbebackend.domain.post.repository;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Boolean existsByJourney(Journey journey);

    List<Post> findAllByJourneyOrderByCreatedAtAsc(Journey journey);
}
