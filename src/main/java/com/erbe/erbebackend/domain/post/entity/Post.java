package com.erbe.erbebackend.domain.post.entity;

import com.erbe.erbebackend.domain.journey.entity.Journey;
import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private Boolean isPublic;

    private String imgUrl;

    private LocalDate createdDate;

    private int photoCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @ManyToOne
    @JoinColumn(name = "journey_id")
    private Journey journey;
}
