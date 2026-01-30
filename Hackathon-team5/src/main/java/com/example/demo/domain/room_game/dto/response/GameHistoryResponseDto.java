package com.example.demo.domain.room_game.dto.response;

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
@Schema(description = "게임 히스토리 응답")
public class GameHistoryResponseDto {

    @Schema(description = "게임 ID", example = "1")
    private Long gameId;

    @Schema(description = "방 ID", example = "108")
    private Long roomId;

    @Schema(description = "게임 이미지 URL")
    private String gameImageUrl;

    @Schema(description = "게임 시작 시간")
    private LocalDateTime playingAt;

    @Schema(description = "게임 종료 시간")
    private LocalDateTime finishedAt;

    @Schema(description = "플레이 시간(초)", example = "1800")
    private Integer playtimeSeconds;

    @Schema(description = "종료 사유", example = "TIME_OVER")
    private String finishReason;

    @Schema(description = "승리 팀", example = "POLICE")
    private String winningTeam;

    @Schema(description = "총 경찰 수", example = "5")
    private Integer totalPolice;

    @Schema(description = "총 도둑 수", example = "5")
    private Integer totalThieves;

    @Schema(description = "검거된 도둑 수", example = "3")
    private Integer caughtThieves;
}
