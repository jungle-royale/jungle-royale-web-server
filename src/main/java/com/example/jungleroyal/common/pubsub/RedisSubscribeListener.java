package com.example.jungleroyal.common.pubsub;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.domain.dto.GameEndMessageDto;
import com.example.jungleroyal.domain.dto.GameStartMessageDto;
import com.example.jungleroyal.service.GameRoomServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


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

            // 채널별 메시지 처리
            switch (channel) {
                case "GameStart" -> handleGameStartMessage(messageBody);
                case "GameEnd" -> handleGameEndMessage(messageBody);
                default -> log.warn("Unknown channel: {}", channel);
            }

            // Return || Another Method Call(etc.save to DB)
            // TODO
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void handleGameStartMessage(String messageBody) throws JsonProcessingException {
        GameStartMessageDto gameStartMessage = objectMapper.readValue(messageBody, GameStartMessageDto.class);
        log.info("Game Start Event: {}", gameStartMessage);
        // TODO: 게임 시작 로직 추가

        // TODO: 게임 시작에 따른 비즈니스 로직 추가
        // 예: 게임 상태 업데이트, 사용자 알림 전송 등
        Long roomId = gameStartMessage.getRoomId();
        log.info("받은 room 정보 : {}", roomId);
        gameRoomService.updateRoomStatus(roomId, RoomStatus.RUNNING);
    }

    private void handleGameEndMessage(String messageBody) throws JsonProcessingException {
        GameEndMessageDto gameEndMessageDto = objectMapper.readValue(messageBody, GameEndMessageDto.class);
        log.info("Game End Event: {}", gameEndMessageDto);
        // TODO: 게임 종료 로직 추가

        Long roomId = gameEndMessageDto.getRoomId();
        log.info("받은 room 정보 : {}", roomId);
        gameRoomService.updateRoomStatus(roomId, RoomStatus.END);
    }
}
