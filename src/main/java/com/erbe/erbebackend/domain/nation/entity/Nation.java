package com.erbe.erbebackend.domain.nation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String krName;

    private String enName;

    private String imgUrl;

    private Double longitude;

    private Double latitude;
}
