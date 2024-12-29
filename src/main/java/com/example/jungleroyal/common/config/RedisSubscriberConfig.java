package com.example.jungleroyal.common.config;

import com.example.jungleroyal.common.pubsub.RedisSubscribeListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriberConfig {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscribeListener redisSubscribeListener;

    @PostConstruct
    public void subscribeToChannels() {
        // GameStart 채널 구독
        redisMessageListenerContainer.addMessageListener(
                redisSubscribeListener,
                new ChannelTopic("GameStart")
        );
        log.info("Subscribed to GameStart channel");
    }
}
