package com.example.jungleroyal.common.pubsub;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.domain.dto.GameStartMessageDto;
import com.example.jungleroyal.domain.dto.MessageDto;
import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.service.GameRoomServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscribeListener implements MessageListener {

    private final RedisTemplate<String, Object> template;
    private final ObjectMapper objectMapper;
    private final GameRoomServiceImpl gameRoomService;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(pattern);
            String messageBody = new String(message.getBody());

            String publishMessage = template
                    .getStringSerializer().deserialize(message.getBody());

            // GameStart 채널 처리
            if ("GameStart".equals(channel)) {
                GameStartMessageDto gameStartMessage = objectMapper.readValue(messageBody, GameStartMessageDto.class);
                handleGameStartMessage(gameStartMessage);

                GameStartMessageDto gameStartMessageDto = objectMapper.readValue(publishMessage, GameStartMessageDto.class);

                log.info("Redis Subscribe Channel : " + gameStartMessageDto.getRoomId());
                log.info("Redis SUB Message : {}", publishMessage);
            }

            // Return || Another Method Call(etc.save to DB)
            // TODO
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void handleGameStartMessage(GameStartMessageDto message) {
        log.info("Game Start Event Received: {}", message);

        // TODO: 게임 시작에 따른 비즈니스 로직 추가
        // 예: 게임 상태 업데이트, 사용자 알림 전송 등
        Long roomId = message.getRoomId();
        log.info("받은 room 정보 : {}", roomId);
        gameRoomService.updateRoomStatus(roomId, RoomStatus.RUNNING);
    }

}
