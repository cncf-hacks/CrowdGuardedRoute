package com.root.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Location implements Comparable<Location> {

    @NotBlank(message = "Customer identification value is required.")
    @Length(max = 6, min = 6, message = "The customer identification key is 6 digits.")
    private String uniqueKey; // 고객 식별값

    private Double latitude; // 위도
    private Double longitude; // 경도

    private Double speed;
    private Double light;

    private String uuid; // 고객 디바이스 값 -> 푸시
    private LocalDateTime registryTime;

    @Builder
    private Location(String uniqueKey, Double latitude, Double longitude, Double speed, Double light, String uuid) {
        this.uniqueKey = uniqueKey;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.light = light;
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {
        return this.uniqueKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if( !(obj instanceof Location test) ) return false;

        return this.uniqueKey.equals(test.getUniqueKey());
    }

    public void setRegistry() {
        this.registryTime = LocalDateTime.now();
    }

    @Override
    public int compareTo(Location o) {
        return this.registryTime.compareTo(o.getRegistryTime());
    }
}