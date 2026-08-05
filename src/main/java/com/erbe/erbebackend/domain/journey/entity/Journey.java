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

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double longitude;

    private Double latitude;
}
