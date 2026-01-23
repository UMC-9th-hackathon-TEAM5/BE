package com.example.demo.domain.room.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "방 생성 요청")
public class CreateRoomRequestDto {

    @Schema(description = "방 제목", example = "홍대 도둑을 찾아라")
    @NotBlank(message = "방 제목은 필수입니다.")
    private String title;

    @Schema(description = "장소 이름", example = "잠실역 2호선")
    @NotBlank(message = "장소 이름은 필수입니다.")
    private String placeName;

    @Schema(description = "주소", example = "서울특별시 송파구 올림픽로 265")
    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @Schema(description = "감옥 장소 이름", example = "잠실역 2호선")
    @NotBlank(message = "감옥 장소 이름은 필수입니다.")
    private String prisonPlaceName;

    @Schema(description = "감옥 주소", example = "서울특별시 송파구 올림픽로 265")
    @NotBlank(message = "감옥 주소는 필수입니다.")
    private String prisonAddress;

    @Schema(description = "만남 시간", example = "2026-06-20T18:00:00")
    @NotNull(message = "만남 시간은 필수입니다.")
    @Future(message = "만남 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime meetingTime;

    @Schema(description = "모집하는 경찰 수", example = "5")
    @NotNull(message = "모집하는 경찰 수는 필수입니다.")
    @Min(value = 1, message = "최소 1명 이상이어야 합니다.")
    private Integer police_capacity;

    @Schema(description = "모집하는 도둑 수", example = "5")
    @NotNull(message = "모집하는 도둑 수 필수입니다.")
    @Min(value = 1, message = "최소 1명 이상이어야 합니다.")
    private Integer thief_capacity;

    @Schema(description = "제한시간(분)", example = "60")
    @NotNull(message = "제한시간은 필수입니다.")
    private Integer countdownSeconds;

    @Schema(description = "도망갈 시간(초)", example = "30")
    @NotNull(message = "제한시간은 필수입니다.")
    private Integer escapeTime;
}
