package com.example.oauthlogin.repository;

import com.example.oauthlogin.domain.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
}
