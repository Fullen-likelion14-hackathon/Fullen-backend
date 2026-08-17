package com.erbe.erbebackend.domain.artist.entity;

import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "artists")
public class Artist extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 300)
    private String imgUrl;

    @Column(nullable = false, length = 500)
    private String stylePrompt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nation_id", nullable = false)
    private Nation nation;

    @Column(nullable = false, length = 100)
    private String introSummary; // 작가 소개 한 줄 요약

    @Column(nullable = false, length = 2000)
    private String description; // 작가 소개 전체 글
}
