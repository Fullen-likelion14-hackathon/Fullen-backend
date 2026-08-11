package com.erbe.erbebackend.domain.patch.entity;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.bag.enums.BagSide;
import com.erbe.erbebackend.domain.order.entity.PatchOrder;
import com.erbe.erbebackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patch_positions")
public class PatchPosition extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_bag_id", nullable = false)
    private UserBag userBag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patch_id", nullable = false)
    private Patch patch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patch_order_id")
    private PatchOrder patchOrder; // null이면 아직 주문 안 된 패치

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BagSide side; // 패치가 부착된 면(앞/뒤)

    @Column(nullable = false)
    private Double posX; // x 좌표

    @Column(nullable = false)
    private Double posY; // y 좌표

    @Column(nullable = false)
    private Double rotation; // 각도

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEditable = true; // 수정 가능 여부 (주문 완료시 false로 변경)

    // 패치 위치 업데이트 메서드
    public void updatePosition(BagSide side, Double posX, Double posY, Double rotation) {
        this.side = side;
        this.posX = posX;
        this.posY = posY;
        this.rotation = rotation;
    }

    // 주문 확정 (해당 위치를 주문에 포함시키고 수정/삭제 불가 상태로 변경)
    public void confirmOrder(PatchOrder patchOrder) {
        this.patchOrder = patchOrder;
        this.isEditable = false;
    }
}
