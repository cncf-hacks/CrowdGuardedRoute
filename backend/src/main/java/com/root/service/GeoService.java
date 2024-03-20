package com.root.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.root.domain.dto.Learning;
import com.root.domain.dto.Location;
import com.root.domain.type.Meta;
import com.root.handler.JsonHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class GeoService {

    private final GeoOperations<String, String> geoOperations;

    private final HashOperations<String, String, Object> hashOperations;

    public GeoService(GeoOperations<String, String> geoOperations, HashOperations<String, String, Object> hashOperations) {
        this.geoOperations = geoOperations;
        this.hashOperations = hashOperations;
    }

    public void add(Meta meta, Location location) {
        geoOperations.add(meta.name(), new Point(location.getLongitude(), location.getLatitude()), location.getUniqueKey());
    }

    public Learning recommendRoute(Circle circle) {
        return new Route(this.geoOperations, this.hashOperations, circle)
                .nearByPoliceStationForDistance()
                .nearByFireStationForDistance()
                .nearByTrafficListForDistance()
                .nearByLampForDistance()
                .nearByPeopleForGeoInfo()
                .build();
    }

    static class Route {
        private static final double defaultPoliceDistance = 4.385;
        private static final double defaultFireDistance = 3.297;
        private static final double defaultTrafficDistance = 0.05;
        private static final double defaultLampDistance = 0.05;

        private final GeoOperations<String, String> geoOperations;

        private final HashOperations<String, String, Object> hashOperations;

        private final Circle circle;

        private final RedisGeoCommands.GeoRadiusCommandArgs distanceArgs = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(1);

        private final RedisGeoCommands.GeoRadiusCommandArgs peopleArg = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();

        private final Learning.LearningBuilder learning = Learning.builder();

        public Route(GeoOperations<String, String> geoOperations, HashOperations<String, String, Object> hashOperations, Circle circle) {
            this.geoOperations = geoOperations;
            this.hashOperations = hashOperations;
            this.circle = circle;
        }

        public Route nearByPoliceStationForDistance() {
            GeoResults<RedisGeoCommands.GeoLocation<String>> police = geoOperations.radius(Meta.POLICE.name(), circle, distanceArgs);
            learning.d2p(Objects.requireNonNull(police).getContent()
                    .stream()
                    .map(GeoResult::getDistance)
                    // Metric unit conversion to MILES
                    .findFirst().orElse(new Distance(defaultPoliceDistance, Metrics.KILOMETERS)).getValue() * 1_000);
            return this;
        }

        public Route nearByFireStationForDistance() {
            GeoResults<RedisGeoCommands.GeoLocation<String>> fire = geoOperations.radius(Meta.FIRE.name(), circle, distanceArgs);
            learning.d2f(Objects.requireNonNull(fire).getContent()
                    .stream()
                    .map(GeoResult::getDistance)
                    // Metric unit conversion to MILES
                    .findFirst().orElse(new Distance(defaultFireDistance, Metrics.KILOMETERS)).getValue() * 1_000);
            return this;
        }

        public Route nearByTrafficListForDistance() {
            GeoResults<RedisGeoCommands.GeoLocation<String>> traffic = geoOperations.radius(Meta.TRAFFIC.name(), circle, distanceArgs);
            learning.d2t(Objects.requireNonNull(traffic).getContent()
                    .stream()
                    .map(GeoResult::getDistance)
                    // Metric unit conversion to MILES
                    .findFirst().orElse(new Distance(defaultTrafficDistance, Metrics.KILOMETERS)).getValue() * 1_000);
            return this;
        }

        public Route nearByLampForDistance() {
            GeoResults<RedisGeoCommands.GeoLocation<String>> lamp = geoOperations.radius(Meta.LAMP.name(), circle, distanceArgs);
            learning.d2l(Objects.requireNonNull(lamp).getContent()
                    .stream()
                    .map(GeoResult::getDistance)
                    // Metric unit conversion to MILES
                    .findFirst().orElse(new Distance(defaultLampDistance, Metrics.KILOMETERS)).getValue() * 1_000);
            return this;
        }

        public Route nearByPeopleForGeoInfo() {

            Circle cir = new Circle(
                    new Point(circle.getCenter().getX(), circle.getCenter().getY()),
                    // max 50m
                    new Distance(Math.min(circle.getRadius().getValue() / 100, 0.05), Metrics.KILOMETERS)
            );

            // nearByPeople
            GeoResults<RedisGeoCommands.GeoLocation<String>> radius = geoOperations.radius(Meta.PEOPLE.name(), cir, peopleArg);

            ObjectMapper objectMapper = JsonHandler.objectMapper();
            TypeReference<List<Location>> typeReference = new TypeReference<>() {};

            List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = Objects.requireNonNull(radius).getContent();

            learning.people(Objects.requireNonNull(content)
                    .stream()
                    .filter(data -> {
                        try {
                            LocalDateTime peopleRegisterTime = objectMapper.convertValue(hashOperations.get("PEOPLE_REGISTER_TIME", data.getContent().getName()), LocalDateTime.class);
                            return Duration.between(LocalDateTime.now(),peopleRegisterTime).toMinutes() <= 5;
                        } catch (Exception e) {
                            log.error("people count cal error ", e);
                            return false;
                        }
                    })
                    .toList()
                    .size());

            learning.speed(Objects.requireNonNull(content)
                    .stream()
                    .mapToDouble(speed -> {
                        try {
                            List<Location> history = objectMapper.convertValue(hashOperations.get("NEARBY_PEOPLE_SPEED", speed.getContent().getName()), typeReference);
                            Location max = Collections.max(history);
                            if (Duration.between(LocalDateTime.now(), max.getRegistryTime()).toMinutes() <= 5)
                                return max.getSpeed();
                            else
                                return 0.0;
                        } catch (Exception e) {
                            log.error("speed info cal error", e);
                            return 0.0;
                        }
                    })
                    .average()
                    .orElse(0.0));

            learning.light(Objects.requireNonNull(content)
                    .stream()
                    .mapToDouble(light -> {
                        try {
                            List<Location> history = objectMapper.convertValue(hashOperations.get("NEARBY_PEOPLE_LIGHT", light.getContent().getName()), typeReference);
                            Location max = Collections.max(history);
                            if (Duration.between(LocalDateTime.now(), max.getRegistryTime()).toMinutes() <= 5)
                                return max.getLight();
                            else
                                return 7 / Math.pow(0.05, 2);
                        } catch (Exception e) {
                            log.error("light info cal error", e);
                            return 7 / Math.pow(0.05, 2);
                        }
                    })
                    .average()
                    .orElse(7 / Math.pow(0.05, 2)));

            return this;
        }
        public Learning build() {
            return learning
                    .latitude(circle.getCenter().getY())
                    .longitude(circle.getCenter().getX())
                    .build();
        }
    }
}
