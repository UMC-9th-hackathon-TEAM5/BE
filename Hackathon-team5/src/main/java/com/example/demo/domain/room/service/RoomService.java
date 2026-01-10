package com.example.demo.domain.room.service;

import com.example.demo.domain.room.dto.request.CreateRoomRequestDto;
import com.example.demo.domain.room.dto.response.CreateRoomResponseDto;
import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.room.entity.enums.RoomStatus;
import com.example.demo.domain.room.repository.RoomRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserService userService;

    public CreateRoomResponseDto createRoom(CreateRoomRequestDto dto, Long userId) {

        if (dto.getPolice_capacity() + dto.getThief_capacity() > dto.getMaxParticipants()){
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }
        User user = userService.findById(userId);
        Room room = Room.builder()
                .host(user)
                .title(dto.getTitle())
                .placeText(dto.getPlaceName())
                .latitude(dto.getLat())
                .longitude(dto.getLng())
                .meetingTime(dto.getMeetingTime())
                .capacityTotal(dto.getMaxParticipants())
                .capacityPolice(dto.getPolice_capacity())
                .capacityThief(dto.getThief_capacity())
                .status(RoomStatus.WAITING)
                .escapeTime(dto.getEscapeTime())
                .countdownSeconds(dto.getCountdownSeconds())
                .build();

        Room savedRoom = roomRepository.save(room);

        return CreateRoomResponseDto.builder()
                .roomId(savedRoom.getId())
                .build();
    }

}
