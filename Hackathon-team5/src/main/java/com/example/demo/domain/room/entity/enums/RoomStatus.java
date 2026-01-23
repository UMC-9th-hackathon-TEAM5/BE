package com.example.demo.domain.room.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomStatus {
    WAITING("대기 중"),
    PLAYING("게임 진행 중");

    private final String description;
}