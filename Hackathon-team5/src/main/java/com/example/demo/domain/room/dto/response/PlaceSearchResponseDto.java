package com.example.demo.domain.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "장소 검색 응답")
public class PlaceSearchResponseDto {

    private List<PlaceItem> places;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PlaceItem {
        @Schema(description = "장소 이름", example = "스타벅스 홍대입구역점")
        private String name;

        @Schema(description = "주소", example = "서울특별시 마포구 양화로 160")
        private String address;
    }
}
