package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRoomRepository {

    boolean existsByHostId(String hostId);

    GameRoomJpaEntity save(GameRoomJpaEntity gameRoomJpaEntity);

    Optional<GameRoomJpaEntity> findById(Long roomId);

    Optional<GameRoomJpaEntity> findByGameUrl(String gameUrl);

    void delete(GameRoomJpaEntity room);

    List<GameRoomJpaEntity> findByUpdatedAtBeforeAndCurrentPlayers(LocalDateTime thresholdTime, int currentPlayers);

    String getGameUrlById(Long roomId);

    List<GameRoomJpaEntity> findAll();

    List<GameRoomJpaEntity> findAllByStatusAndCurrentPlayers(RoomStatus roomStatus, int currentPlayers);

    void deleteAll(List<GameRoomJpaEntity> emptyRooms);
}
