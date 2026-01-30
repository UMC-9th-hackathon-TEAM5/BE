package com.example.demo.domain.room_game.repository;

import com.example.demo.domain.room_game.entity.GameHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {

    // 특정 방의 게임 히스토리 조회 (최신순)
    @Query("SELECT gh FROM GameHistory gh WHERE gh.room.id = :roomId ORDER BY gh.finishedAt DESC")
    List<GameHistory> findByRoomIdOrderByFinishedAtDesc(@Param("roomId") Long roomId);

    // 특정 방의 최근 N개 게임 히스토리 조회 (Pageable 사용)
    @Query("SELECT gh FROM GameHistory gh WHERE gh.room.id = :roomId ORDER BY gh.finishedAt DESC")
    List<GameHistory> findRecentGamesByRoomId(@Param("roomId") Long roomId, Pageable pageable);
}
