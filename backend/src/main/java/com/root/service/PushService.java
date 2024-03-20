package com.root.service;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import com.root.domain.dto.Push;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PushService {

    @Value("${spring.data.push.domain}")
    private String domain;

    public void sendPushMessage(Push dto) {

        Map<String, String> pushDto = new HashMap<>();
        pushDto.put("to", dto.getPushKey());
        pushDto.put("title", dto.getTitle());
        pushDto.put("body", dto.getBody());

        JSONObject jsonObject = JsonHandler.objectMapper().convertValue(pushDto, JSONObject.class);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(domain))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        try {
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("res : {}", send.body());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
