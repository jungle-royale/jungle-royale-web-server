package com.example.jungleroyal.common.config;

import com.example.jungleroyal.common.pubsub.RedisSubscribeListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriberConfig {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscribeListener redisSubscribeListener;
    @PostConstruct
    public void subscribeToChannels() {
        // 구독할 채널 리스트
        List<ChannelTopic> topics = List.of(
                new ChannelTopic("GameStart"),
                new ChannelTopic("GameEnd")
        );

        // GameStart 채널 구독
        // 여러 채널을 구독
        for (ChannelTopic topic : topics) {
            redisMessageListenerContainer.addMessageListener(redisSubscribeListener, topic);
            log.info("Subscribed to channel: {}", topic.getTopic());
        }
    }
}
