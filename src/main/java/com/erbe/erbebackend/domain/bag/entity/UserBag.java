package com.erbe.erbebackend.domain.bag.entity;

import com.erbe.erbebackend.domain.nation.entity.Nation;
import com.erbe.erbebackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate useStartDate;

    @OneToOne
    @JoinColumn(name = "bag_product_id")
    private BagProduct bagProduct;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
