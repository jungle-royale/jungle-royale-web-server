package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;

import java.util.List;
import java.util.Optional;

public interface GameRoomRepository {

    boolean existsByHostId(String hostId);

    GameRoomJpaEntity save(GameRoomJpaEntity gameRoomJpaEntity);

    Optional<GameRoomJpaEntity> findById(Long roomId);

    Optional<GameRoomJpaEntity> findByGameUrl(String gameUrl);

    void delete(GameRoomJpaEntity room);


    String getGameUrlById(Long roomId);

    List<GameRoomJpaEntity> findAll();
}
