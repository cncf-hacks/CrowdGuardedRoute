package com.root.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Score {

    private Double lat; // 위도
    private Double lon; // 경도
    private Double score;

    @Builder
    private Score(Double latitude, Double longitude, Double score) {
        this.lon = latitude;
        this.lat = longitude;
        this.score = score;
    }
}
