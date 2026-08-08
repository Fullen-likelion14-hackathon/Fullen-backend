package com.erbe.erbebackend.domain.patch.entity;

import com.erbe.erbebackend.domain.patch.enums.PatchType;
import com.erbe.erbebackend.domain.user.entity.User;
import com.erbe.erbebackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patches")
public class Patch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PatchType type;

    @Column(nullable = false, length = 300)
    private String imgUrl; // 커스텀 패치 이미지 url

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
