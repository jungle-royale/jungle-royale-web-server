package com.example.jungleroyal.common.util;

import com.example.jungleroyal.domain.game.GameServerNotificationResponse;
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

    @Value("${game-server.base-url}/rooms/notify")
    private String gameServerUrl;

//    private static final String GAME_SERVER_URL = "http://game-server/api/rooms/notify"; // 게임 서버 URL

    public GameServerNotificationResponse notifyGameServer(String userId) {
        log.info("Sending notification to game server , userId: [{}]", userId);

        try {
            ResponseEntity<GameServerNotificationResponse> response = restTemplate.postForEntity(
                    gameServerUrl,
                    null,
                    GameServerNotificationResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("게임 서버 응답이 올바르지 않습니다.");
            }

            log.info("Received response from game server: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error while communicating with the game server", e);
            throw new IllegalStateException("게임 서버와 통신 중 문제가 발생했습니다.", e);
        }
    }
}
