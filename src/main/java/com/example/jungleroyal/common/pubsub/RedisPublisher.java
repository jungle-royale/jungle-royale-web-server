package com.example.jungleroyal.common.pubsub;

import com.example.jungleroyal.domain.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {
//    private final RedisTemplate<String, Object> template;
//
//    public void publish(ChannelTopic topic, Object message){
//        log.info("Publishing to channel: {} | Message: {}", topic.getTopic(), message);
//        template.convertAndSend(topic.getTopic(), message);
//    }
//
//    public void publish(ChannelTopic topic, String data){
//        log.info("topic : " + topic);
//        log.info("data : " + data);
//        template.convertAndSend(topic.getTopic(), data);
//    }
}

