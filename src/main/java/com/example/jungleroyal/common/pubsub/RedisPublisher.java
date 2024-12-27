package com.example.jungleroyal.common.pubsub;

import com.example.jungleroyal.domain.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> template;

    public RedisPublisher(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    public void publish(ChannelTopic topic, MessageDto dto){
        template.convertAndSend(topic.getTopic(), dto);
    }

    public void publish(ChannelTopic topic, String data){
        template.convertAndSend(topic.getTopic(), data);
    }
}

