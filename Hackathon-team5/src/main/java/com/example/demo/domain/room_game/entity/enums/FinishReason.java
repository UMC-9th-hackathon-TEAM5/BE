package com.example.demo.domain.room_game.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FinishReason {
    TIME_OVER("시간 초과"),
    GAME_END("게임 종료");

    private final String description;
}