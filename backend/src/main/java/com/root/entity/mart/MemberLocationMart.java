package com.root.entity.mart;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberLocationMart {

    @Id
    private String identifier;

    @Column(precision = 50, scale = 10)
    private BigDecimal latitude; // 위도

    @Column(precision = 50, scale = 10)
    private BigDecimal longitude; // 경도

    @Column(precision = 10, scale = 5)
    private BigDecimal speed;

    @Column(precision = 50, scale = 10)
    private BigDecimal light;

    private LocalDateTime registry;

    @Builder
    private MemberLocationMart(
            String identifier,
            BigDecimal latitude,
            BigDecimal longitude,
            BigDecimal speed,
            BigDecimal light,
            LocalDateTime registry) {
        this.identifier = identifier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.light = light;
        this.registry = registry;
    }
}
