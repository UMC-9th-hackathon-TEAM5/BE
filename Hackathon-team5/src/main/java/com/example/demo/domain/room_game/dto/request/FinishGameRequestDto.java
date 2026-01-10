package com.example.demo.domain.room_game.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게임 종료 요청")
public class FinishGameRequestDto {

    @Schema(description = "종료 이유 (TIME_OVER, GAME_END)", example = "GAME_END")
    @NotNull(message = "종료 이유는 필수입니다.")
    private String finishReason;

    @Schema(description = "승리 팀 (POLICE, THIEF, DRAW)", example = "POLICE")
    @NotNull(message = "승리 팀은 필수입니다.")
    private String winningTeam;
}
