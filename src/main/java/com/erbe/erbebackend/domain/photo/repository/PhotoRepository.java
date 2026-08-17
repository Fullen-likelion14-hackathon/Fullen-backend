package com.erbe.erbebackend.domain.photo.repository;

import com.erbe.erbebackend.domain.nation.enums.Continent;
import com.erbe.erbebackend.domain.photo.entity.Photo;
import com.erbe.erbebackend.domain.post.entity.Post;
import com.erbe.erbebackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByPostOrderBySeqAsc(Post post);

    List<Photo> findAllByPost(Post post);

    List<Photo> findAllByPostNationContinentAndPostUserOrderByPostCreatedDate(Continent scope, User user);

    List<Photo> findAllByPostUserOrderByPostCreatedDate(User postUser);
}
