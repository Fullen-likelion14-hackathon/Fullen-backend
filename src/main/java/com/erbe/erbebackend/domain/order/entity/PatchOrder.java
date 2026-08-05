package com.erbe.erbebackend.domain.order.entity;

import com.erbe.erbebackend.domain.bag.entity.UserBag;
import com.erbe.erbebackend.domain.order.enums.OrderStatus;
import com.erbe.erbebackend.domain.patch.entity.Patch;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double posX;

    private Double posY;

    private Double rotation;

    private LocalDate createdAt;

    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "user_bag_id")
    private UserBag userBag;

    @OneToOne
    @JoinColumn(name = "patch_id")
    private Patch patch;

}