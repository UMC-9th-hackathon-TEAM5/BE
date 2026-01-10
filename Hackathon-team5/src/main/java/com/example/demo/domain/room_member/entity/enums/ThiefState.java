package com.example.demo.domain.room_member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThiefState {
    ALIVE("생존"),
    CAUGHT("검거됨");

    private final String description;
}