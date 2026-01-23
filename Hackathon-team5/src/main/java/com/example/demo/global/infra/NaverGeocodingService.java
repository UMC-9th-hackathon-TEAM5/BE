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

    public NaverGeocodingService(
            @Value("${naver.maps.client-id}") String clientId,
            @Value("${naver.maps.client-secret}") String clientSecret

    ) {
        log.info("==================================================");
        log.info(">>> [DEBUG] Naver Maps Client ID: {}", clientId);
        
        log.info(">>> [DEBUG] Naver Maps Secret: {}", clientSecret);
        log.info("==================================================");

        this.webClient = WebClient.builder()
                .baseUrl("https://naveropenapi.apigw.ntruss.com/map-geocode/v2")
                .defaultHeader("X-NCP-APIGW-API-KEY-ID", clientId.trim())
                .defaultHeader("X-NCP-APIGW-API-KEY", clientSecret.trim())
                .build();
    }

    public BigDecimal[] getCoordinates(String address) {
        Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geocode")
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new BusinessException(ErrorCode.GEOCODING_FAILED);
        }

        List<Map<String, Object>> addresses = (List<Map<String, Object>>) response.get("addresses");

        if (addresses == null || addresses.isEmpty()) {
            throw new BusinessException(ErrorCode.GEOCODING_FAILED);
        }

        Map<String, Object> first = addresses.get(0);
        BigDecimal lng = new BigDecimal((String) first.get("x"));
        BigDecimal lat = new BigDecimal((String) first.get("y"));

        return new BigDecimal[]{lat, lng};
    }
}
