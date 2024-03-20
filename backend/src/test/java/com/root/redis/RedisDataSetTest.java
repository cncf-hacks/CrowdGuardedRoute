package com.root.redis;

import com.root.domain.dto.Location;
import com.root.domain.type.Meta;
import com.root.entity.mart.*;
import com.root.repository.mart.*;
import com.root.service.GeoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class RedisDataSetTest {

    @Autowired
    GeoService geoService;

    @Autowired
    TrafficRepository trafficRepository;

    @Autowired
    FireStationRepository fireStationRepository;

    @Autowired
    PoliceStationRepository policeStationRepository;

    @Autowired
    LampRepository lampRepository;

    @Autowired
    MemberLocationRepository memberLocationRepository;

    @Autowired
    HashOperations<String, String, Object> hashOperations;

    @Test
    void trafficDataSaveTest() {
        List<TrafficMart> all = trafficRepository.findAll();
        all.forEach(data -> geoService.add(Meta.TRAFFIC, Location.builder()
                .uniqueKey(data.getIdentifier())
                .latitude(data.getLatitude().doubleValue())
                .longitude(data.getLongitude().doubleValue())
                .build()));
    }

    @Test
    void fireStationDataSaveTest() {
        List<FireStationMart> all = fireStationRepository.findAll();
        all.forEach(data -> geoService.add(Meta.FIRE, Location.builder()
                .uniqueKey(data.getIdentifier())
                .latitude(data.getLatitude().doubleValue())
                .longitude(data.getLongitude().doubleValue())
                .build()));
    }

    @Test
    void policeStationDataSaveTest() {
        List<PoliceStationMart> all = policeStationRepository.findAll();
        all.forEach(data -> geoService.add(Meta.POLICE, Location.builder()
                .uniqueKey(data.getIdentifier())
                .latitude(data.getLatitude().doubleValue())
                .longitude(data.getLongitude().doubleValue())
                .build()));
    }

    @Test
    void lampDataSaveTest() {
        List<LampMart> all = lampRepository.findAll();
        all.forEach(data -> geoService.add(Meta.LAMP, Location.builder()
                .uniqueKey(data.getIdentifier())
                .latitude(data.getLatitude().doubleValue())
                .longitude(data.getLongitude().doubleValue())
                .build()));
    }

    @Test
    void locationDataSaveTest() {
        List<MemberLocationMart> all = memberLocationRepository.findAll();
        all.forEach(data -> {

            Location location = Location.builder()
                    .uniqueKey(data.getIdentifier())
                    .latitude(data.getLatitude().doubleValue())
                    .longitude(data.getLongitude().doubleValue())
                    .build();
            location.setRegistry();

            geoService.add(Meta.PEOPLE, location);
            hashOperations.put("PEOPLE_REGISTER_TIME", data.getIdentifier(), LocalDateTime.now());

        });
    }

    @Test
    void locationSpeedDataSaveTest() {
        List<MemberLocationMart> all = memberLocationRepository.findAll();
        all.forEach(data -> {
            Location location = Location.builder()
                    .uniqueKey(data.getIdentifier())
                    .latitude(data.getLatitude().doubleValue())
                    .longitude(data.getLongitude().doubleValue())
                    .speed(data.getSpeed().doubleValue())
                    .build();

            location.setRegistry();

            hashOperations.put("NEARBY_PEOPLE_SPEED", data.getIdentifier(), List.of(location));
            hashOperations.put("PEOPLE_REGISTER_TIME", data.getIdentifier(), LocalDateTime.now());
        });
    }

    @Test
    void locationLightDataSaveTest() {
        List<MemberLocationMart> all = memberLocationRepository.findAll();
        all.forEach(data -> {
            Location location = Location.builder()
                    .uniqueKey(data.getIdentifier())
                    .latitude(data.getLatitude().doubleValue())
                    .longitude(data.getLongitude().doubleValue())
                    .light(data.getLight().doubleValue())
                    .build();

            location.setRegistry();

            hashOperations.put("NEARBY_PEOPLE_LIGHT", data.getIdentifier(), List.of(location));
            hashOperations.put("PEOPLE_REGISTER_TIME", data.getIdentifier(), LocalDateTime.now());
        });
    }

}
