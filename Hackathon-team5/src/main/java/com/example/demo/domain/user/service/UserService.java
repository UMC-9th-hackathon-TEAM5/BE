package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.request.UpdateLocationRequestDto;
import com.example.demo.domain.user.dto.response.UpdateLocationResponseDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return user;
    }

    @Transactional
    public UpdateLocationResponseDto updateLocation(Long userId, UpdateLocationRequestDto request) {
        // 1. 사용자 조회
        User user = getUserById(userId);

        // 2. 위치 업데이트
        user.updateLocation(request.getLat(), request.getLng());

        // 3. 응답 생성
        return UpdateLocationResponseDto.builder()
                .userId(user.getId())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .success(true)
                .build();
    }
}
