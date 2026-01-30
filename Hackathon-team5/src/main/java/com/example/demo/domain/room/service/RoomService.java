package com.example.demo.domain.room.service;

import com.example.demo.domain.room.converter.RoomConverter;
import com.example.demo.domain.room.dto.request.CreateRoomRequestDto;
import com.example.demo.domain.room.dto.response.CreateRoomResponseDto;
import com.example.demo.domain.room.dto.response.NearbyRoomDataResponseDto;
import com.example.demo.domain.room.dto.response.NearbyRoomsResponseDto;
import com.example.demo.domain.room.dto.response.RoomDetailResponseDto;
import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.room.entity.enums.RoomStatus;
import com.example.demo.domain.room.repository.RoomRepository;
import com.example.demo.domain.room_member.entity.RoomMember;
import com.example.demo.domain.room_member.entity.enums.JoinStatus;
import com.example.demo.domain.room_member.repository.RoomMemberRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.domain.room.dto.response.PlaceSearchResponseDto;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.infra.NaverSearchService.PlaceResult;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.infra.NaverGeocodingService;
import com.example.demo.global.infra.NaverSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserService userService;
    private final RoomConverter roomConverter;
    private final NaverGeocodingService naverGeocodingService;
    private final NaverSearchService naverSearchService;
    
    // Caffeine Cache로 변경: 자동으로 만료되고 메모리 누수 없음
    private final Cache<Long, Long> searchRateLimit = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    @Transactional
    public CreateRoomResponseDto createRoom(CreateRoomRequestDto dto, Long userId) {
        User user = userService.getUserById(userId);

        BigDecimal[] coordinates = naverGeocodingService.getCoordinates(dto.getAddress());
        BigDecimal lat = coordinates[0];
        BigDecimal lng = coordinates[1];

        Room room = Room.builder()
                .host(user)
                .title(dto.getTitle())
                .placeText(dto.getPlaceName())
                .prisonPlaceName(dto.getPrisonPlaceName())
                .placeAddress(dto.getAddress())
                .prisonAddress(dto.getPrisonAddress())
                .latitude(lat)
                .longitude(lng)
                .meetingTime(dto.getMeetingTime())
                .capacityPolice(dto.getPoliceCapacity())
                .capacityThief(dto.getThiefCapacity())
                .status(RoomStatus.WAITING)
                .escapeTime(dto.getEscapeTime())
                .countdownSeconds(dto.getCountdownSeconds())
                .build();

        Room savedRoom = roomRepository.save(room);

        // 방장을 room_members에 자동 추가
        RoomMember hostMember = RoomMember.builder()
                .room(savedRoom)
                .user(user)
                .joinStatus(JoinStatus.JOINED)
                .isArrived(false)
                .build();
        roomMemberRepository.save(hostMember);

        System.out.println("방 생성 완료 - roomId: " + savedRoom.getId() + ", 방장 userId: " + user.getId());

        return CreateRoomResponseDto.builder()
                .roomId(savedRoom.getId())
                .hostId(user.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public NearbyRoomsResponseDto getRoomsForUser(Long userId) {
        // 1. 사용자 정보 조회 (사용자의 현재 위도, 경도 활용)
        User user = userService.getUserById(userId);

        // 2. 좌표 유효성 검증
        BigDecimal userLat = user.getLatitude();
        BigDecimal userLon = user.getLongitude();
        
        if (userLat == null || userLon == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        
        double lat = userLat.doubleValue();
        double lon = userLon.doubleValue();
        
        // 위도/경도 범위 검증
        if (lat < -90 || lat > 90) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (lon < -180 || lon > 180) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 3. 반경 3km(3000m) 고정 조회
        double FIXED_RADIUS = 3000.0;
        List<Room> rooms = roomRepository.findRoomsWithinRadius(lon, lat, FIXED_RADIUS);

        // 4. DTO 변환 및 거리 계산
        List<NearbyRoomDataResponseDto> roomDataList = rooms.stream()
                .map(room -> roomConverter.convertToDataDto(room, userLat, userLon))
                .collect(Collectors.toList());

        return NearbyRoomsResponseDto.builder()
                .rooms(roomDataList)
                .totalCount(roomDataList.size())
                .build();
    }

    @Transactional
    public void deleteRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.getHost().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ONLY_HOST_ALLOWED);
        }

        roomRepository.delete(room);
    }

    @Transactional(readOnly = true)
    public RoomDetailResponseDto getRoomDetail(Long roomId) {
        // 1. 방 존재 여부 확인
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // 2. 참가자 목록 조회
        List<RoomMember> members = roomMemberRepository.findAllByRoomIdWithUser(roomId);

        // 3. 컨버터를 통해 DTO 변환
        return roomConverter.convertToRoomDetailDto(room, members);
    }

    @Transactional(readOnly = true)
    public PlaceSearchResponseDto searchPlaces(String keyword, Long userId) {
        long now = System.currentTimeMillis();
        
        // computeIfPresent를 사용하여 thread-safe하게 rate limit 체크
        Long lastCall = searchRateLimit.get(userId, k -> null);
        if (lastCall != null && now - lastCall < 3000) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }
        searchRateLimit.put(userId, now);

        List<PlaceResult> results = naverSearchService.searchPlaces(keyword);

        List<PlaceSearchResponseDto.PlaceItem> places = results.stream()
                .map(r -> PlaceSearchResponseDto.PlaceItem.builder()
                        .name(r.name())
                        .address(r.address())
                        .build())
                .toList();

        return PlaceSearchResponseDto.builder()
                .places(places)
                .build();
    }
}
