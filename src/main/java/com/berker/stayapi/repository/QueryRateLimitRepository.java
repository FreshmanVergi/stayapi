package com.berker.stayapi.repository;

import com.berker.stayapi.model.QueryRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface QueryRateLimitRepository extends JpaRepository<QueryRateLimit, Long> {
    Optional<QueryRateLimit> findByIdentifierAndQueryDate(String identifier, LocalDate queryDate);
}
