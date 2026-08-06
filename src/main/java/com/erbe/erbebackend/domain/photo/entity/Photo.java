package com.erbe.erbebackend.domain.photo.entity;

import com.erbe.erbebackend.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.geo.Point;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int seq;

    private String imgUrl;

    private Double longitude;

    private Double latitude;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}
