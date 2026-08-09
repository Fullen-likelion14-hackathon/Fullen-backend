package com.erbe.erbebackend.domain.journey.entity;

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
public class Journey extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    private String coverImgUrl;

    private String firstImgUrl;

    // 반정규화 필드이지만, 여행 조회때마다 PostRepository로 쿼리를 보내는 것보다 효율적이라 판단함
    private int postCount;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double longitude;

    private Double latitude;

    public void updateFirstImageUrl(String firstImageUrl) {
        this.firstImgUrl = firstImageUrl;
    }

    public void incrementPostCount() {
        this.postCount++;
    }

    public void decreasePostCount() {
        this.postCount--;
    }
}
