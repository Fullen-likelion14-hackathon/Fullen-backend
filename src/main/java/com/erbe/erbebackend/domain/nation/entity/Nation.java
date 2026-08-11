package com.erbe.erbebackend.domain.nation.entity;

import com.erbe.erbebackend.domain.nation.enums.Continent;
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

    private Continent continent;
}
