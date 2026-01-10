package com.example.demo.domain.room_member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    POLICE("경찰"),
    THIEF("도둑");

    private final String description;
}