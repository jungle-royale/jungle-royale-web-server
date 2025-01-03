package com.example.jungleroyal.service;

import com.example.jungleroyal.common.pubsub.RedisPublisher;
import com.example.jungleroyal.common.pubsub.RedisSubscribeListener;
import com.example.jungleroyal.domain.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPubService {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisPublisher redisPublisher;

    private final RedisSubscribeListener redisSubscribeListener;

    /**
     * Channel 별 Message 전송
     * @param
     */
    public void pubMsgChannel(String channel , MessageDto message) {

        //1. 요청한 Channel 을 구독.
        redisMessageListenerContainer.addMessageListener(redisSubscribeListener, new ChannelTopic(channel));

        //2. Message 전송
        redisPublisher.publish(new ChannelTopic(channel), message);
    }

    public void cancelSubChannel(String channel) {
        redisMessageListenerContainer.removeMessageListener(redisSubscribeListener);
    }
}
