package com.example.jungleroyal.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRoomRepositoryImpl implements GameRoomRepository{
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
    public String getGameUrlById(Long roomId) {
        return gameRoomJpaRepository.getGameUrlById(roomId);
    }

    @Override
    public List<GameRoomJpaEntity> findAll() {
        return gameRoomJpaRepository.findAll();
    }
}
