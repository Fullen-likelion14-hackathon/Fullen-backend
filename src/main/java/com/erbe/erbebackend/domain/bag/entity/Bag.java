package com.erbe.erbebackend.domain.bag.entity;

import com.erbe.erbebackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bags")
public class Bag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String material;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(nullable = false, length = 50)
    private String size;

    @Column(nullable = false, length = 300)
    private String frontImgUrl; // 가방 앞면 사진

    @Column(nullable = false, length = 300)
    private String backImgUrl; // 가방 뒷면 사진
}