package com.example.jungleroyal.common.scheduler;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
import com.example.jungleroyal.service.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomScheduler {

    private final GameRoomRepository gameRoomRepository;

//    @Scheduled(cron = "*/10 * * * * *") // 10분
//    public void runTaskWithCron() {
//        gameRoomRepository
//                .findByUpdatedAtBeforeAndCurrentPlayers(
//                        LocalDateTime.now().minusMinutes(3),
//                        0
//                )
//                .stream()
//                .forEach(room -> {
//                    log.info(room.toString());
//                    room.setStatus(RoomStatus.END);
//                    gameRoomRepository.save(room);
//                });
//    }
}
