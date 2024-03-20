package com.root.entity.mart;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrafficMart {

//    @Id
//    @GenericGenerator(
//            name = "SequenceGenerator",
//            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
//            parameters = {
//
//                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "hibernate_sequence"),
//                    @org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled"),
//                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
//                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "500")
//            }
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "SequenceGenerator"
//    )
    @Id
    private String identifier;

    @Column(precision = 50, scale = 10)
    private BigDecimal latitude; // 위도

    @Column(precision = 50, scale = 10)
    private BigDecimal longitude; // 경도

    private String name;

    private String address;

    @Builder
    private TrafficMart(
            String identifier,
            BigDecimal latitude,
            BigDecimal longitude,
            String name,
            String address) {
        this.identifier = identifier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
    }
}
