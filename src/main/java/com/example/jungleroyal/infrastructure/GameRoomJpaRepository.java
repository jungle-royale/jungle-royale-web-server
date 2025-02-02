package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.common.types.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRoomJpaRepository extends JpaRepository<GameRoomJpaEntity, Long> {
    Optional<GameRoomJpaEntity> findByGameUrl(String gameUrl);

    boolean existsByHostId(String hostId);

    @Query("SELECT gr.gameUrl FROM GameRoomJpaEntity gr WHERE gr.id = :roomId")
    String getGameUrlById(Long roomId);

    List<GameRoomJpaEntity> findAllByStatusAndCurrentPlayers(RoomStatus roomStatus, int currentPlayers);

    List<GameRoomJpaEntity> findByUpdatedAtBeforeAndCurrentPlayers(LocalDateTime thresholdTime, int currentPlayers);
}
