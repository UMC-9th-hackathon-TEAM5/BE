package com.example.demo.domain.room_member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JoinStatus {
    JOINED("참여 완료"),
    VERIFIED("출석 완료");

    private final String description;
}