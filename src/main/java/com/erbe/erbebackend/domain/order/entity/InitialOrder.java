package com.erbe.erbebackend.domain.order.entity;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.order.enums.OrderStatus;
import com.erbe.erbebackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "initial_orders")
public class InitialOrder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_bag_id", nullable = false)
    private UserBag userBag;

    @Column(nullable = false, length = 50)
    private String initialPhrase; // 이니셜 문구

    @Column(nullable = false, length = 20)
    private String color; // Color 객체 X -> ex "#33ff11"

    @Column(nullable = false)
    private boolean isBold; // 굵기 여부 (true : 굵게, false : 기본)
}
