package com.example.demo.domain.room_member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도착 상태 변경 응답")
public class ArrivalToggleResponseDto {

    @Schema(description = "대상 사용자 ID", example = "2")
    private Long targetUserId;

    @Schema(description = "대상 사용자 닉네임", example = "라이언")
    private String targetNickname;

    @Schema(description = "변경된 도착 상태 (true: 도착, false: 미도착)", example = "true")
    private Boolean isArrived;
}
