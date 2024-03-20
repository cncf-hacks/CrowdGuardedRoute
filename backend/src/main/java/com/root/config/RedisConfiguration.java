package com.root.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.root.handler.JsonHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration {

    @Bean
    public HashOperations<String, String, Object> redisTemplateForHash(RedisProperties properties) {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(properties.getHost(), properties.getPort());
        lettuceConnectionFactory.setDatabase(7);
        lettuceConnectionFactory.afterPropertiesSet();

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        ObjectMapper objectMapper = JsonHandler.objectMapper();

        GenericJackson2JsonRedisSerializer keySerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate.opsForHash();
    }

    @Bean
    public GeoOperations<String, String> geoOperations(RedisTemplate<String, String> template) {
        return template.opsForGeo();
    }

}
