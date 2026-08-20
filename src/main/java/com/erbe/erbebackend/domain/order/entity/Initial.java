package com.erbe.erbebackend.domain.order.entity;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.bag.enums.BagSide;
import com.erbe.erbebackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "initials")
public class Initial extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_bag_id", nullable = false)
    private UserBag userBag;

    @Column(nullable = false, length = 20)
    private String initialPhrase; // 이니셜 문구

    @Column(nullable = false, length = 20)
    private String color; // Color 객체 X -> ex "#33ff11"

    @Column(nullable = false)
    private boolean isBold; // 굵기 여부 (true : 굵게, false : 기본)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BagSide side; // 이니셜이 부착된 면(앞/뒤)

    @Column(nullable = false)
    private Double posX; // x 좌표

    @Column(nullable = false)
    private Double posY; // y 좌표

    @Column(nullable = false)
    private Double rotation; // 각도

    @Column(nullable = false)
    private Double scale; // 이니셜 크기 배율

    @Column(nullable = false)
    private Integer layer; // 겹칠 때 위에 표시되는 순서 (클수록 위)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // null이면 아직 주문 안 된 이니셜

    // 주문 확정
    public void confirmOrder(Order order) {
        this.order = order;
    }

    // 이니셜 수정
    public void updateInitial(String color, boolean isBold, BagSide side, Double posX, Double posY, Double rotation, Double scale, Integer layer) {
        this.color = color;
        this.isBold = isBold;
        this.side = side;
        this.posX = posX;
        this.posY = posY;
        this.rotation = rotation;
        this.scale = scale;
        this.layer = layer;
    }
}
