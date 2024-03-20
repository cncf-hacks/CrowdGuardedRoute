package com.root.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import com.root.domain.dto.Learning;
import com.root.domain.dto.Rank;
import com.root.domain.dto.Score;
import com.root.handler.JsonHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MLService {

    @Value("${spring.data.ml.domain}")
    private String domain;

    public List<Rank> scoring(List<List<Learning>> learns) {

        ObjectMapper objectMapper = JsonHandler.objectMapper();

        Map<String, List<List<Learning>>> sd = new HashMap<>();
        sd.put("list", learns);

        JSONObject jsonObject = objectMapper.convertValue(sd, JSONObject.class);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain + "/post_json"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        try {
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject body = objectMapper.readValue(send.body(), JSONObject.class);
            TypeReference<List<List<Score>>> typeReference = new TypeReference<>() {};
            List<List<Score>> scores = objectMapper.convertValue(body.get("result"), typeReference);
            AtomicInteger atomicInteger = new AtomicInteger(1);

            return scores.stream()
                    .map(score -> Rank.builder()
                            .score(score)
                            .sum(score
                                    .stream()
                                    .mapToDouble(Score::getScore)
                                    .sum())
                            .build())
                    .sorted(Comparator.comparing(Rank::getSum))
                    .peek(sort -> sort.setRank(atomicInteger.getAndIncrement()))
                    .toList();


        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
