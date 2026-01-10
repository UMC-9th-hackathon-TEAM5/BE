package com.example.demo.domain.room_game.dto.response;

import com.example.demo.domain.room_member.entity.enums.ThiefState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게임 현황 응답")
public class GameStatusResponseDto {

    @Schema(description = "시작 시간", example = "2026-01-10T15:54:00")
    private LocalDateTime startTime;

    @Schema(description = "게임 종료 시간", example = "2026-01-10T16:30:00")
    private LocalDateTime endTime;

    @Schema(description = "참가자 목록")
    private List<GameParticipant> participants;



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "참가자 게임 정보")
    public static class GameParticipant {

        @Schema(description = "사용자 ID", example = "1")
        private Long userId;

        @Schema(description = "닉네임", example = "도둑잡는사자")
        private String nickname;

        @Schema(description = "역할 (POLICE, THIEF)", example = "THIEF")
        private String role;

        @Schema(description = "생존 여부", example = "true")
        private ThiefState isAlive;

        @Schema(description = "도착 여부", example = "true")
        private Boolean isArrived;

        @Schema(description = "경찰일 경우에만 잡은 횟수 반환")
        private Integer caughtCount;
    }
}
