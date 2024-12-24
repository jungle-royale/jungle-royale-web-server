package com.example.oauthlogin.repository;

import com.example.oauthlogin.domain.gameroom.GameRoom;
import com.example.oauthlogin.domain.gameroom.GameRoomJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRoomRepository extends JpaRepository<GameRoomJpaEntity, Long> {
}
