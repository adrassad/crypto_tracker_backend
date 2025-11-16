package com.adrassad.cryprice.scheduler.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Простой fetcher, использующий CoinGecko public API.
 */
@Service
public class CryptoPriceFetcherService {

    private final WebClient webClient;

    public CryptoPriceFetcherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.coingecko.com/api/v3").build();
    }

    /**
     * Возвращает Map id->priceInUsd для указанных CoinGecko ids (idsCsv = "bitcoin,ethereum").
     */
    public Mono<Map<String, Double>> fetchPricesByIds(String idsCsv) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/simple/price")
                        .queryParam("ids", idsCsv)
                        .queryParam("vs_currencies", "usd")
                        .build())
                .retrieve()
                // исправлено: используем HttpStatusCode::isError или лямбду
                .onStatus(HttpStatusCode::isError,
                        resp -> Mono.error(new RuntimeException("Failed to fetch prices: " + resp.statusCode())))
                // десериализуем в Map<String, Map<String, Double>>
                .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                .map(nested -> {
                    Map<String, Double> result = new HashMap<>();
                    if (nested == null) return result;
                    for (Map.Entry<String, Map<String, Double>> e : nested.entrySet()) {
                        String id = e.getKey();
                        Map<String, Double> inner = e.getValue();
                        if (inner != null) {
                            Double usd = inner.get("usd");
                            if (usd != null) {
                                result.put(id, usd);
                            } else {
                                // если нет usd — можно положить 0 или пропустить
                                result.put(id, 0.0);
                            }
                        } else {
                            result.put(id, 0.0);
                        }
                    }
                    return result;
                });
    }
}