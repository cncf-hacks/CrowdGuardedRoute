package com.root.controller;

import lombok.RequiredArgsConstructor;
import com.root.domain.dto.Location;
import com.root.domain.dto.Rank;
import com.root.domain.io.CommonBo;
import com.root.domain.io.CommonVo;
import com.root.service.GeoService;
import com.root.service.MLService;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/location/route")
public class RouteController {

    private final GeoService geoService;
    private final MLService mlService;

    @PostMapping("/v1/recommend")
    public CommonVo<List<Rank>> scoring(@RequestBody @Validated CommonBo<List<List<Location>>> location) {

        List<Rank> ranks = mlService.scoring(location.getPayload()
                .stream()
                .map(list -> list
                        .stream()
                        .map(l -> geoService.recommendRoute(
                                        new Circle(
                                                new Point(l.getLongitude(), l.getLatitude()),
                                                new Distance(5.0, Metrics.KILOMETERS))
                                )
                        )
                        .toList()
                )
                .toList()
        );

        return CommonVo.<List<Rank>>builder()
                .payload(ranks)
                .message("User creating Success")
                .build();
    }
}

