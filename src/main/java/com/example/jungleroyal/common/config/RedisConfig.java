package com.example.jungleroyal.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    private final Environment environment;

    public RedisConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        String activeProfile = environment.getProperty("spring.profiles.active");
        if ("dev".equals(activeProfile)) {
            config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        } else if ("prod".equals(activeProfile)) {
            config.useSingleServer().setAddress("redis://jungle-royale-redis.u8i6dt.ng.0001.use2.cache.amazonaws.com:6379");
        } else {
            throw new IllegalArgumentException("Unknown profile: " + activeProfile);
        }

        return Redisson.create(config);
    }
}
