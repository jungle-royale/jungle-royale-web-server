package com.example.oauthlogin.repository;

import com.example.oauthlogin.domain.gameroom.GameRoom;
import com.example.oauthlogin.domain.gameroom.GameRoomJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRoomRepository extends JpaRepository<GameRoomJpaEntity, Long> {
    Optional<GameRoomJpaEntity> findByGameUrl(String gameUrl);

    boolean existsByHostId(String hostId);
}
