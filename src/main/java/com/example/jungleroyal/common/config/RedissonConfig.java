package com.example.jungleroyal.common.config;

import jakarta.annotation.PostConstruct;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${spring.data.redis.mode}")
    private String redisMode;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.master:}") // 배포 환경에서만 사용
    private String masterAddress;

    @Value("${spring.data.redis.replicas:}") // 배포 환경에서만 사용
    private String[] slaveAddresses;

    @PostConstruct
    public void checkRedisConfig() {
        System.out.println("Redis Mode: " + redisMode);
        System.out.println("Redis Host: " + redisHost);
        System.out.println("Redis Port: " + redisPort);
        System.out.println("Master Address: " + masterAddress);
        System.out.println("Slave Addresses: " + (slaveAddresses != null ? String.join(", ", slaveAddresses) : "None"));
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        if ("single".equalsIgnoreCase(redisMode)) {
            // 단일 서버 모드
            config.useSingleServer()
                    .setAddress(String.format("redis://%s:%d", redisHost, redisPort))
                    .setConnectionPoolSize(64)
                    .setConnectionMinimumIdleSize(10)
                    .setTimeout(3000);
        } else if ("master-slave".equalsIgnoreCase(redisMode)) {
            // Master-Slave 모드
            config.useMasterSlaveServers()
                    .setMasterAddress(masterAddress)
                    .addSlaveAddress(slaveAddresses)
                    .setReadMode(ReadMode.SLAVE)
                    .setTimeout(3000)
                    .setRetryAttempts(3)
                    .setRetryInterval(1500);
        }

        return Redisson.create(config);
    }
}
