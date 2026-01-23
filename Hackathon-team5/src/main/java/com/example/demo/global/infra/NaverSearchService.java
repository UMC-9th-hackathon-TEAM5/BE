package com.example.demo.global.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NaverSearchService {

    private final WebClient webClient;

    public NaverSearchService(
            @Value("${naver.search.client-id}") String clientId,
            @Value("${naver.search.client-secret}") String clientSecret
    ) {
        this.webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    public List<PlaceResult> searchPlaces(String keyword) {
        Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/local.json")
                        .queryParam("query", keyword)
                        .queryParam("display", 5)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(item -> {
                    String title = ((String) item.get("title")).replaceAll("<[^>]*>", "");
                    String roadAddress = (String) item.get("roadAddress");
                    String address = (String) item.get("address");
                    String displayAddress = (roadAddress != null && !roadAddress.isEmpty()) ? roadAddress : address;
                    return new PlaceResult(title, displayAddress);
                })
                .toList();
    }

    public record PlaceResult(String name, String address) {}
}
