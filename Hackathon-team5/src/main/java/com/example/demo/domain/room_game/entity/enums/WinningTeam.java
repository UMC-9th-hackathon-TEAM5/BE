package com.example.demo.domain.room_game.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WinningTeam {
    POLICE("경찰 승리"),
    THIEF("도둑 승리"),
    DRAW("무승부");

    private final String description;
}