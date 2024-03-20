package com.root.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import com.root.entity.mart.*;
import com.root.repository.mart.*;
import com.root.repository.service.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;


@SpringBootTest
public class DataMart {

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
    MemberRepository memberRepository;


    @Test
    void readLampRawDataByFile() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader("temp/eclairage-public.geojson");
        JSONObject parsed = (JSONObject) parser.parse(fileReader);

        JSONArray array = (JSONArray) parsed.get("features");

        List<LampMart> list = array.stream()
                .map(element -> LampMart.builder()
                        .identifier(UUID.randomUUID().toString().substring(0, 6))
                        .name("Lamp_" + ((JSONObject)((JSONObject) element).get("properties")).get("foyer") )
                        .address((String) ((JSONObject)((JSONObject) element).get("properties")).get("lib_region") )
                        .latitude(BigDecimal.valueOf( (double) ((JSONObject) ((JSONObject) ((JSONObject) element).get("properties")).get("geo_point_2d")).get("lat") ))
                        .longitude(BigDecimal.valueOf( (double) ((JSONObject) ((JSONObject) ((JSONObject) element).get("properties")).get("geo_point_2d")).get("lon") ))
                        .build()
                )
//                .limit(10)
                .toList();

        lampRepository.saveAll(list);

    }

    @Test
    void readTrafficLightRawDataByFile() throws IOException, InterruptedException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader("temp/signalisation-tricolore.geojson");
        JSONObject parsed = (JSONObject) parser.parse(fileReader);

        JSONArray array = (JSONArray) parsed.get("features");

        List<TrafficMart> list = array.stream()
                .map(element -> TrafficMart.builder()
                        .identifier(UUID.randomUUID().toString().substring(0, 6))
                        .name("Traffic_Light_" + ((JSONObject)((JSONObject) element).get("properties")).get("foyer") )
                        .address((String) ((JSONObject)((JSONObject) element).get("properties")).get("lib_region") )
                        .latitude(BigDecimal.valueOf( (double) ((JSONObject) ((JSONObject) ((JSONObject) element).get("properties")).get("geo_point_2d")).get("lat") ))
                        .longitude(BigDecimal.valueOf( (double) ((JSONObject) ((JSONObject) ((JSONObject) element).get("properties")).get("geo_point_2d")).get("lon") ))
                        .build()
                )
                .toList();

        trafficRepository.saveAll(list);
    }

    @Test
    void readPoliceRawDataByFile() throws IOException, CsvException {
        // CSV 에외 케이스로 라이브러리 포맷 셋
        CSVReader csvReader = new CSVReader(new FileReader("temp/police.csv"));

        List<PoliceStationMart> datalist = csvReader.readAll().stream()
                .map(line -> {
                    List<String> list = Arrays.asList(line);

                    return PoliceStationMart.builder()
                            .identifier(UUID.randomUUID().toString().substring(0, 6))
                            .name("Police_Station")
                            .address(list.get(1))
                            .latitude(new BigDecimal(list.get(2)))
                            .longitude(new BigDecimal(list.get(3)))
                            .build();
                })
                .toList();

        policeStationRepository.saveAll(datalist);
    }

    @Test
    void readFireRawDataByFile() throws IOException, CsvException {
        // CSV 에외 케이스로 라이브러리 포맷 셋
        CSVReader csvReader = new CSVReader(new FileReader("temp/firestation.csv"));

        List<FireStationMart> datalist = csvReader.readAll().stream()
                .map(line -> {
                    List<String> list = Arrays.asList(line);

//                    String s = list.get(3);
//                    System.out.println("s = " + s);

                    return FireStationMart.builder()
                            .identifier(UUID.randomUUID().toString().substring(0, 6))
                            .name("Fire_Station")
                            .address(list.get(2))
                            .latitude(new BigDecimal(list.get(3)))
                            .longitude(new BigDecimal(list.get(4)))
                            .build();
                })
                .toList();

        fireStationRepository.saveAll(datalist);
    }

    @Test
    void createMemberLocationForMock() {
        List<MemberLocationMart> list = IntStream.range(0, 1000)
                .mapToObj(index -> MemberLocationMart.builder()
                        .identifier(UUID.randomUUID().toString().substring(0, 6))
                        .speed(BigDecimal.valueOf(Math.random() * 10))
//                        .latitude(BigDecimal.valueOf(48 + Math.random()))
//                        .longitude(BigDecimal.valueOf(2 + Math.random()))
                        .latitude( new BigDecimal( "48.85" + (int) (Math.random() * 1000) + 1 ) )
                        .longitude( new BigDecimal("2.29" + (int) (Math.random() * 1000) + 1) )
                        .light(BigDecimal.valueOf(Math.random() * 100))
                        .registry(LocalDateTime.now())
                        .build())
                .toList();

        memberLocationRepository.saveAll(list);

    }

}
