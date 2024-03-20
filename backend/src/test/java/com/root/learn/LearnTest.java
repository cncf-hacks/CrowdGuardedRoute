package com.root.learn;

import com.root.domain.dto.Learning;
import com.root.domain.dto.Location;
import com.root.service.GeoService;
import com.root.service.MLService;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class LearnTest {

    @Autowired
    GeoService geoService;

    @Autowired
    MLService mlService;

//    @Test
    @RepeatedTest(value = 5)
    void LearnTest() {

        // 48.8583, 2.2945
        List<List<Location>> locations =
                IntStream.range(0, 3)
                        .mapToObj(idx ->
                                IntStream.range(0, 3)
                                        .mapToObj(idx2 -> Location.builder()
//                                                .latitude(48 + Math.random())
//                                                .longitude(2 + Math.random())
                                                        .latitude( Double.valueOf( "48.85" + (int) (Math.random() * 100) + 1 ) )
                                                        .longitude( Double.valueOf("2.29" + (int) (Math.random() * 100) + 1) )
                                                .build()
                                        ).toList()
                        ).toList();

        List<List<Learning>> list = locations.stream()
                .map(l -> l.stream()
                        .map(lo -> geoService.recommendRoute(
                            new Circle(
                                new Point(lo.getLongitude(), lo.getLatitude()),
                                new Distance(5.0, Metrics.KILOMETERS)
                            )))
                        .toList()
                ).toList();

        System.out.println("locations = " + list);


//        mlService.scoring(list);
    }
}
