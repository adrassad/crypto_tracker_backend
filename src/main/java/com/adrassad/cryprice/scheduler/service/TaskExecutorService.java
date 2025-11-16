package com.adrassad.cryprice.scheduler.service;

import com.adrassad.cryprice.scheduler.entity.CryptoInfo;
import com.adrassad.cryprice.scheduler.entity.ScheduledTask;
import com.adrassad.cryprice.scheduler.repository.CryptoInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * Исполняет конкретные задачи по имени. Сейчас реализован handler для "fetch_crypto_prices".
 * Task params ожидаются в JSON: { "ids": "bitcoin,ethereum", "source": "coingecko" }
 */
@Service
public class TaskExecutorService {

    private final CryptoPriceFetcherService fetcher;
    private final CryptoInfoRepository cryptoRepo;
    private final ObjectMapper objectMapper;

    public TaskExecutorService(CryptoPriceFetcherService fetcher,
                               CryptoInfoRepository cryptoRepo,
                               ObjectMapper objectMapper) {
        this.fetcher = fetcher;
        this.cryptoRepo = cryptoRepo;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> execute(ScheduledTask task) {
        String name = task.getName();
        if ("fetch_crypto_prices".equals(name)) {
            return executeFetchCryptoPrices(task);
        }
        // Здесь можно добавить другие типы задач
        return Mono.empty();
    }

    /**
     * Ожидаем, что fetcher вернёт Map<String, Object> или Map<String, Map<String, Double>>,
     * т.к. CoinGecko отдаёт: { "bitcoin": { "usd": 12345.0 }, "ethereum": { "usd": 345.0 } }
     */
    @SuppressWarnings("unchecked")
    private Mono<Void> executeFetchCryptoPrices(ScheduledTask task) {
        try {
            JsonNode params = objectMapper.readTree(task.getParams() == null ? "{}" : task.getParams());
            String ids = params.has("ids") ? params.get("ids").asText() : "bitcoin";
            String source = params.has("source") ? params.get("source").asText() : "coingecko";

            if (!"coingecko".equalsIgnoreCase(source)) {
                return Mono.empty();
            }

            // Предпочтительно: изменить fetcher.fetchPricesByIds чтобы возвращал
            // Mono<Map<String, Map<String, Double>>>; но тут парсим динамически
            return fetcher.fetchPricesByIds(ids)
                    .flatMapMany(map -> {
                        // map может быть Map<String, Object> с вложенными Map'ами
                        return Flux.fromIterable(map.entrySet());
                    })
                    .flatMap(entry -> {
                        String id = entry.getKey();
                        Object value = entry.getValue();
                        Double price = null;

                        if (value instanceof Map) {
                            Map<?, ?> inner = (Map<?, ?>) value;
                            Object usd = inner.get("usd");
                            if (usd instanceof Number) {
                                price = ((Number) usd).doubleValue();
                            } else if (usd != null) {
                                try {
                                    price = Double.valueOf(usd.toString());
                                } catch (NumberFormatException ignore) {
                                    price = 0.0;
                                }
                            }
                        } else if (value instanceof Number) {
                            price = ((Number) value).doubleValue();
                        } else if (value != null) {
                            // на случай, если fetcher вернул Map<String, Double> (где value - Double)
                            try {
                                price = Double.valueOf(value.toString());
                            } catch (NumberFormatException ignore) {
                                price = 0.0;
                            }
                        } else {
                            price = 0.0;
                        }

                        CryptoInfo info = new CryptoInfo();
                        info.setSymbol(id);
                        info.setPrice(price == null ? 0.0 : price);
                        info.setRecordedAt(OffsetDateTime.now(ZoneOffset.UTC));
                        info.setSource("coingecko");
                        info.setMeta(null);

                        // Репозиторий JPA блокирующий — выполняем сохранение на boundedElastic
                        return Mono.fromCallable(() -> cryptoRepo.save(info))
                                .subscribeOn(Schedulers.boundedElastic())
                                .then();
                    })
                    .then();

        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }
}