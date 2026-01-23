package com.example.demo.domain.room.scheduler;

import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomCleanupScheduler {

    private final RoomRepository roomRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredRooms() {
        List<Room> expiredRooms = roomRepository.findByMeetingTimeBefore(LocalDateTime.now().minusDays(1));

        if (expiredRooms.isEmpty()) {
            return;
        }

        log.info("만료된 방 {}개 삭제 시작", expiredRooms.size());
        roomRepository.deleteAll(expiredRooms);
        log.info("만료된 방 {}개 삭제 완료", expiredRooms.size());
    }
}
