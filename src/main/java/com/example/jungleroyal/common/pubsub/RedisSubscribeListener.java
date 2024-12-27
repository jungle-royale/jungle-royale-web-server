package com.example.jungleroyal.common.pubsub;

import com.example.jungleroyal.domain.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscribeListener implements MessageListener {

    private final RedisTemplate<String, Object> template;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = template
                    .getStringSerializer().deserialize(message.getBody());

            MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);

            log.info("Redis Subscribe Channel : " + messageDto.getRoomId());
            log.info("Redis SUB Message : {}", publishMessage);

            // Return || Another Method Call(etc.save to DB)
            // TODO
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

}
