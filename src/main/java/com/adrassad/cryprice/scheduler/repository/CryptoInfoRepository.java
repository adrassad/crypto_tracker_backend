package com.adrassad.cryprice.scheduler.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adrassad.cryprice.scheduler.entity.CryptoInfo;

public interface CryptoInfoRepository extends JpaRepository<CryptoInfo, Long> {
    List<CryptoInfo> findBySymbolAndRecordedAtBetween(String symbol, OffsetDateTime from, OffsetDateTime to);
}