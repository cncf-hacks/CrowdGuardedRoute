package com.root.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private final String host;
    private final int port;

    public RedisProperties(@DefaultValue("localhost") String host, @DefaultValue("6379") int port) {
        this.host = host;
        this.port = port;
    }
}
