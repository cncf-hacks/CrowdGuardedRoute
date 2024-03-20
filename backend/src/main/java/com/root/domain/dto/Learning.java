package com.root.domain.dto;

import lombok.Getter;

@Getter
public class Learning {

    private final double lat; // 위도

    private final double lon; // 경도

    private final int people;

    private final int walkorrun; // 속도

    private final double illuminance; // 조도

    private final double d2ps; // 경찰서와의 거리
    private final double d2fs; // 소방서와의 거리
    private final double d2tl; // 신호등와의 거리
    private final double d2l; // 가로등와의 거리

    private Learning(
            double latitude,
            double longitude,
            int people,
            double speed,
            double light,
            double d2p,
            double d2f,
            double d2t,
            double d2l
    ) {
        this.lat = latitude;
        this.lon = longitude;
        this.people = people;
        this.walkorrun = speed > 8.0 ? 1 : 0;
        this.illuminance = light;
        this.d2ps = d2p;
        this.d2fs = d2f;
        this.d2tl = d2t;
        this.d2l = d2l;
    }

    public static LearningBuilder builder() {
        return new LearningBuilder();
    }

    public static class LearningBuilder {
        private double latitude;
        private double longitude;
        private int people;
        private double speed;
        private double light;
        private double d2p;
        private double d2f;
        private double d2t;
        private double d2l;

        LearningBuilder() {
        }

        public LearningBuilder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public LearningBuilder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public LearningBuilder people(int people) {
            this.people = people;
            return this;
        }

        public LearningBuilder speed(double speed) {
            this.speed = speed;
            return this;
        }

        public LearningBuilder light(double light) {
            this.light = light;
            return this;
        }

        public LearningBuilder d2p(double d2p) {
            this.d2p = d2p;
            return this;
        }

        public LearningBuilder d2f(double d2f) {
            this.d2f = d2f;
            return this;
        }

        public LearningBuilder d2t(double d2t) {
            this.d2t = d2t;
            return this;
        }

        public LearningBuilder d2l(double d2l) {
            this.d2l = d2l;
            return this;
        }

        public Learning build() {
            return new Learning(this.latitude, this.longitude, this.people, this.speed, this.light, this.d2p, this.d2f, this.d2t, this.d2l);
        }

        public String toString() {
            return "Learning.LearningBuilder(latitude=" + this.latitude + ", longitude=" + this.longitude + ", people=" + this.people + ", speed=" + this.speed + ", light=" + this.light + ", d2p=" + this.d2p + ", d2f=" + this.d2f + ", d2t=" + this.d2t + ", d2l=" + this.d2l + ")";
        }
    }
}
