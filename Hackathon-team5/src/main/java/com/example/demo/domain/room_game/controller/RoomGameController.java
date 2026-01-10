package com.example.demo.domain.room_game.controller;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.domain.room_game.dto.request.FinishGameRequestDto;
import com.example.demo.domain.room_game.dto.response.GameStatusResponseDto;
import com.example.demo.domain.room_game.entity.enums.FinishReason;
import com.example.demo.domain.room_game.entity.enums.WinningTeam;
import com.example.demo.domain.room_game.service.RoomGameService;
import com.example.demo.global.config.SwaggerConfig;
import com.example.demo.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms/{roomId}/game")
@RequiredArgsConstructor
@Tag(name = "Room Game", description = "게임 상태 관련 API")
public class RoomGameController {

    private final RoomGameService roomGameService;

    @PostMapping("/finish")
    @Operation(summary = "게임 종료", description = "게임을 종료하고 결과를 반환합니다.")
    @SwaggerConfig.ApiErrorExamples({
            ErrorCode.ROOM_NOT_FOUND,
            ErrorCode.ROOM_NOT_IN_PLAYING_STATUS,
            ErrorCode.GAME_STATE_NOT_FOUND,
            ErrorCode.INVALID_INPUT_VALUE
    })
    public ApiResponse<GameStatusResponseDto> finishGame(
            @Parameter(description = "방 ID") @PathVariable Long roomId,
            @Valid @RequestBody FinishGameRequestDto request) {

        FinishReason finishReason = FinishReason.valueOf(request.getFinishReason());
        WinningTeam winningTeam = WinningTeam.valueOf(request.getWinningTeam());

        GameStatusResponseDto response = roomGameService.finishGame(roomId, finishReason, winningTeam);

        return ApiResponse.success(response);
    }
}
