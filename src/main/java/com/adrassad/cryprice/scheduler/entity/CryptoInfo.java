package com.adrassad.cryprice.scheduler.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "crypto_info",
       indexes = {@Index(name = "idx_symbol_ts", columnList = "symbol, recorded_at")})
public class CryptoInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BTC, ETH etc
    @Column(nullable = false)
    private String symbol;

    // Цена в базовой валюте (например USD)
    @Column(nullable = false)
    private Double price;

    // Дата/время записи
    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    // Источник (coingecko, binance ...)
    private String source;

    // Доп. json, null если не нужно
    @Column(length = 2048)
    private String meta;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public OffsetDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(OffsetDateTime recordedAt) { this.recordedAt = recordedAt; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getMeta() { return meta; }
    public void setMeta(String meta) { this.meta = meta; }
}