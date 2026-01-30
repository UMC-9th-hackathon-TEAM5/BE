package com.example.demo.domain.room_game.service;

import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.room.entity.enums.RoomStatus;
import com.example.demo.domain.room.repository.RoomRepository;
import com.example.demo.domain.room_game.dto.response.GameHistoryResponseDto;
import com.example.demo.domain.room_game.dto.response.GameStatusResponseDto;
import com.example.demo.domain.room_game.entity.GameHistory;
import com.example.demo.domain.room_game.entity.RoomGameState;
import com.example.demo.domain.room_game.entity.enums.FinishReason;
import com.example.demo.domain.room_game.entity.enums.WinningTeam;
import com.example.demo.domain.room_game.repository.GameHistoryRepository;
import com.example.demo.domain.room_game.repository.RoomGameStateRepository;
import com.example.demo.domain.room_member.entity.RoomMember;
import com.example.demo.domain.room_member.entity.enums.Role;
import com.example.demo.domain.room_member.entity.enums.ThiefState;
import com.example.demo.domain.room_member.repository.RoomMemberRepository;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.websocket.service.WebSocketMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomGameService {

    private final RoomGameStateRepository roomGameStateRepository;
    private final GameHistoryRepository gameHistoryRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final WebSocketMessageService webSocketMessageService;

    @Transactional
    public GameStatusResponseDto finishGame(Long roomId, FinishReason finishReason, WinningTeam winningTeam) {
        // 1. 방 존재 여부 확인
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // 2. 방 상태 확인: PLAYING 상태일 때만 종료 가능
        if (room.getStatus() != RoomStatus.PLAYING) {
            throw new BusinessException(ErrorCode.ROOM_NOT_IN_PLAYING_STATUS);
        }

        // 3. RoomGameState 조회
        RoomGameState gameState = roomGameStateRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_STATE_NOT_FOUND));

        // 4. 게임 종료 처리
        gameState.finishGame(finishReason, winningTeam);

        // 5. 참가자 목록 조회 (통계 계산용)
        List<RoomMember> members = roomMemberRepository.findAllByRoomIdWithUser(roomId);
        
        // 6. 게임 통계 계산
        long totalPolice = members.stream().filter(m -> m.getRole() == Role.POLICE).count();
        long totalThieves = members.stream().filter(m -> m.getRole() == Role.THIEF).count();
        long caughtThieves = members.stream()
                .filter(m -> m.getRole() == Role.THIEF && m.getThiefState() == ThiefState.CAUGHT)
                .count();

        // 7. 게임 히스토리 저장 (종료된 게임 정보 보관)
        GameHistory gameHistory = GameHistory.builder()
                .room(room)
                .gameImageUrl(gameState.getGameImageUrl())
                .playingAt(gameState.getPlayingAt())
                .finishedAt(gameState.getFinishedAt())
                .playtimeSeconds(gameState.getPlaytimeSeconds())
                .finishReason(finishReason)
                .winningTeam(winningTeam)
                .totalPolice((int) totalPolice)
                .totalThieves((int) totalThieves)
                .caughtThieves((int) caughtThieves)
                .build();
        gameHistoryRepository.save(gameHistory);
        
        // 8. RoomGameState 초기화 (다음 게임을 위해)
        gameState.resetForNewGame();

        // 9. 방 상태를 WAITING으로 변경
        room.updateStatus(RoomStatus.WAITING);

        // 10. 모든 참가자의 게임 상태 초기화 (역할, 도둑 상태, 검거 정보 등)
        members.forEach(RoomMember::resetGameState);

        // 8. 참가자 정보 변환
        List<GameStatusResponseDto.GameParticipant> participants = members.stream()
                .map(member -> GameStatusResponseDto.GameParticipant.builder()
                        .userId(member.getUser().getId())
                        .nickname(member.getUser().getNickname())
                        .role(member.getRole() != null ? member.getRole().name() : null)
                        .isAlive(member.getThiefState())
                        .isArrived(member.getIsArrived())
                        .caughtCount(member.getCaughtCount())
                        .build())
                .collect(Collectors.toList());

        // 9. 응답 DTO 생성
        GameStatusResponseDto response = GameStatusResponseDto.builder()
                .startTime(gameState.getPlayingAt())
                .endTime(gameState.getFinishedAt())
                .participants(participants)
                .build();

        // 10. WebSocket으로 게임 종료 이벤트 전송
        webSocketMessageService.sendEventToRoom(roomId, "GAME_FINISHED", response);

        return response;
    }
    @Transactional
    public void saveState(RoomGameState roomGameState) {
        // room을 통해 roomId 가져오기
        Long roomId = roomGameState.getRoom().getId();

        // 이미 존재하는지 확인
        roomGameStateRepository.findById(roomId).ifPresentOrElse(
            existingState -> {
                // 이미 존재하면 게임 시작 시간만 업데이트 (없을 경우)
                if (existingState.getPlayingAt() == null) {
                    existingState.updatePlayingTime(roomGameState.getPlayingAt());
                    System.out.println("게임 시작 시간 업데이트: roomId=" + roomId);
                }
            },
            () -> {
                // 존재하지 않으면 새로 생성
                roomGameStateRepository.save(roomGameState);
                System.out.println("게임 상태 생성: roomId=" + roomId);
            }
        );
    }

    public List<GameHistoryResponseDto> getGameHistories(Long roomId) {
        List<GameHistory> histories = gameHistoryRepository.findByRoomIdOrderByFinishedAtDesc(roomId);
        
        return histories.stream()
                .map(history -> GameHistoryResponseDto.builder()
                        .gameId(history.getId())
                        .roomId(roomId)
                        .gameImageUrl(history.getGameImageUrl())
                        .playingAt(history.getPlayingAt())
                        .finishedAt(history.getFinishedAt())
                        .playtimeSeconds(history.getPlaytimeSeconds())
                        .finishReason(history.getFinishReason().name())
                        .winningTeam(history.getWinningTeam().name())
                        .totalPolice(history.getTotalPolice())
                        .totalThieves(history.getTotalThieves())
                        .caughtThieves(history.getCaughtThieves())
                        .build())
                .collect(Collectors.toList());
    }
}
