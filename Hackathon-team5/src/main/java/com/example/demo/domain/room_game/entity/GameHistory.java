package com.example.demo.domain.room_game.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.room_game.entity.enums.FinishReason;
import com.example.demo.domain.room_game.entity.enums.WinningTeam;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GameHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "game_image_url", columnDefinition = "TEXT")
    private String gameImageUrl;

    @Column(name = "playing_at", nullable = false)
    private LocalDateTime playingAt;

    @Column(name = "finished_at", nullable = false)
    private LocalDateTime finishedAt;

    @Column(name = "playtime_seconds", nullable = false)
    private Integer playtimeSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinishReason finishReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WinningTeam winningTeam;

    @Column(name = "total_police")
    private Integer totalPolice;

    @Column(name = "total_thieves")
    private Integer totalThieves;

    @Column(name = "caught_thieves")
    private Integer caughtThieves;
}
