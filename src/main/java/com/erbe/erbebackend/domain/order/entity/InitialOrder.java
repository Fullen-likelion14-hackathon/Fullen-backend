package com.erbe.erbebackend.domain.order.entity;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitialOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String initial; // TODO: 이니셜 문장 필드명 다시 생각해보기

    private String color; // Color 객체 X -> ex "#33ff11"

    private boolean isBold;

    private LocalDate createdAt;

    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "user_bag_id")
    private UserBag userBag;
}
