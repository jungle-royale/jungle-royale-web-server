package com.example.jungleroyal.common.util;

import com.example.jungleroyal.domain.game.GameServerNotificationRequest;
import com.example.jungleroyal.domain.game.GameServerNotificationResponse;
import com.example.jungleroyal.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameServerClient {
    private final RestTemplate restTemplate;
    private final GameRoomService gameRoomService;

    @Value("${game-server.base-url}/create-game")
    private String gameServerUrl;

//    private static final String GAME_SERVER_URL = "http://game-server/api/"; // 게임 서버 URL

    public GameServerNotificationResponse notifyGameServer(GameServerNotificationRequest gameServerNotificationRequest, String userId) {
        log.info("🍎Sending notification to game server , userId: [{}]", userId);
        log.info("🍎Sending notification to game server , url: [{}]", gameServerUrl);

        try {
            ResponseEntity<GameServerNotificationResponse> response = restTemplate.postForEntity(
                    gameServerUrl,
                    gameServerNotificationRequest,
                    GameServerNotificationResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                gameRoomService.deleteRoom(gameServerNotificationRequest.getRoomId());
                throw new IllegalStateException("게임 서버 응답이 올바르지 않습니다.");
            }

            log.info("Received response from game server: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            gameRoomService.deleteRoom(gameServerNotificationRequest.getRoomId());
            log.error("Error while communicating with the game server", e);
            throw new IllegalStateException("게임 서버와 통신 중 문제가 발생했습니다.", e);
        }
    }
}
