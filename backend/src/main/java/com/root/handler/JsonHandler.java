package com.root.handler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class JsonHandler {


    public static ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")))
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS"))
                .build()
                .registerModules(new ParameterNamesModule(), simpleModule());
    }

    public static SimpleModule simpleModule() {
        SimpleModule simpleModule = new SimpleModule();
        // LocalDate
        simpleModule.addDeserializer(LocalDate.class, new JsonDeserializer<>() {
            @Override
            public LocalDate deserialize(JsonParser p, DeserializationContext cx) throws IOException, JacksonException {
                return LocalDate.parse(p.getValueAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        });
        simpleModule.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
                generator.writeString(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(value));
            }
        });
        // LocalTime
        simpleModule.addDeserializer(LocalTime.class, new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser p, DeserializationContext cx) throws IOException, JacksonException {
                return LocalTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            }
        });
        simpleModule.addSerializer(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public void serialize(LocalTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
                generator.writeString(DateTimeFormatter.ofPattern("HH:mm:ss").format(value));
            }
        });
        // LocalDateTime
        simpleModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext cx) throws IOException, JacksonException {
                return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            }
        });

        simpleModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
                generator.writeString(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(value));
            }
        });

        return simpleModule;
    }
}
