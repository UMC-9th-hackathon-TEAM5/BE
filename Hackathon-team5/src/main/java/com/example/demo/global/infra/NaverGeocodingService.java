package com.example.demo.global.infra;

import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NaverGeocodingService {

    private final WebClient webClient;
    private final String apiKey;

    public NaverGeocodingService(
            @Value("${kakao.rest-api-key}") String apiKey
    ) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .defaultHeader("Authorization", "KakaoAK " + apiKey)
                .build();
    }

    public BigDecimal[] getCoordinates(String address) {
        Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new BusinessException(ErrorCode.GEOCODING_FAILED);
        }

        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");

        if (documents == null || documents.isEmpty()) {
            throw new BusinessException(ErrorCode.GEOCODING_FAILED);
        }

        Map<String, Object> first = documents.get(0);
        BigDecimal lng = new BigDecimal((String) first.get("x"));
        BigDecimal lat = new BigDecimal((String) first.get("y"));

        return new BigDecimal[]{lat, lng};
    }
}
