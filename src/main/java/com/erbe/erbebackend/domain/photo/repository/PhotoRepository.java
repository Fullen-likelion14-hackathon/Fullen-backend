package com.erbe.erbebackend.domain.photo.repository;

import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByPost(Post post);
}
