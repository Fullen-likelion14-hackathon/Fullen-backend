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

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false; // 내 패치함에서 삭제됐는지 (가방에 붙어있으면 Hard Delete 대신 isDeleted 필드로 Soft Delete)

    // 내 패치함에서 삭제 처리
    public void softDelete() {
        this.isDeleted = true;
    }
}
