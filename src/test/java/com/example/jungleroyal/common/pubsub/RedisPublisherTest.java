package com.example.jungleroyal.common.pubsub;

import com.example.jungleroyal.domain.game.ComplexMessage;
import com.example.jungleroyal.domain.game.GameEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class RedisPublisherTest {

    @Autowired
    private RedisPublisher redisPublisher;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testPublishMessages() throws Exception {
        // 1. 단순 문자열 메시지 발행
        redisPublisher.publishMessage("game-events", "hello");

        // 2. JSON 메시지 발행
        GameEvent gameEvent = new GameEvent("player1", "joined", "2024-12-27T20:50:00");
        String jsonMessage = objectMapper.writeValueAsString(gameEvent); // DTO를 JSON으로 변환
        redisPublisher.publishMessage("game-events", jsonMessage);

        // 3. 복잡한 JSON 메시지 발행
        ComplexMessage complexMessage = new ComplexMessage("event123", gameEvent, "extra-data");
        String complexJsonMessage = objectMapper.writeValueAsString(complexMessage);
        redisPublisher.publishMessage("game-events", complexJsonMessage);

        // 4. 숫자나 기타 타입 발행
        redisPublisher.publishMessage("game-events", 42);

        // 5. JSON으로 래핑하지 않은 HashMap 메시지 발행
        Map<String, Object> mapMessage = new HashMap<>();
        mapMessage.put("playerName", "player2");
        mapMessage.put("score", 100);
        redisPublisher.publishMessage("game-events", objectMapper.writeValueAsString(mapMessage));

    }


}