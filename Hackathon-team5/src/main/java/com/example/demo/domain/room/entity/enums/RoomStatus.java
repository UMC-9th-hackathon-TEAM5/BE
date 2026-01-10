package com.example.demo.domain.room.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomStatus {
    WAITING("대기 중"),
    STARTING("시작 준비 중"),
    PLAYING("게임 진행 중"),
    FINISHED("게임 종료");

    private final String description;
}