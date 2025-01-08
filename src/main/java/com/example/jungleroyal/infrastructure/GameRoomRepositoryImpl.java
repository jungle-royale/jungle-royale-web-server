package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.service.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRoomRepositoryImpl implements GameRoomRepository {
    private final GameRoomJpaRepository gameRoomJpaRepository;

    @Override
    public boolean existsByHostId(String hostId) {
        return gameRoomJpaRepository.existsByHostId(hostId);
    }

    @Override
    public GameRoomJpaEntity save(GameRoomJpaEntity gameRoomJpaEntity) {
        return gameRoomJpaRepository.save(gameRoomJpaEntity);
    }

    @Override
    public Optional<GameRoomJpaEntity> findById(Long roomId) {
        return gameRoomJpaRepository.findById(roomId);
    }

    @Override
    public Optional<GameRoomJpaEntity> findByGameUrl(String gameUrl) {
        return gameRoomJpaRepository.findByGameUrl(gameUrl);
    }

    @Override
    public void delete(GameRoomJpaEntity room) {
        gameRoomJpaRepository.delete(room);
    }

    @Override
    public List<GameRoomJpaEntity> findByUpdatedAtBeforeAndCurrentPlayers(LocalDateTime thresholdTime, int currentPlayers) {
        return gameRoomJpaRepository.findByUpdatedAtBeforeAndCurrentPlayers(thresholdTime, currentPlayers);
    }

    @Override
    public String getGameUrlById(Long roomId) {
        return gameRoomJpaRepository.getGameUrlById(roomId);
    }

    @Override
    public List<GameRoomJpaEntity> findAll() {
        return gameRoomJpaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));   // 최신순 정렬
    }

    @Override
    public List<GameRoomJpaEntity> findAllByStatusAndCurrentPlayers(RoomStatus roomStatus, int currentPlayers) {
        return gameRoomJpaRepository.findAllByStatusAndCurrentPlayers(roomStatus, currentPlayers);
    }

    @Override
    public void deleteAll(List<GameRoomJpaEntity> emptyRooms) {
        gameRoomJpaRepository.deleteAll(emptyRooms);
    }
}
