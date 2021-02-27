package com.github.chaitriplez.openstreaming.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("symbolInfo")
@Data
public class SymbolInfo {
  @Id private String symbol;
  private BigDecimal highPx;
  private BigDecimal lowPx;
  private BigDecimal lastPx;
  private Long totalQty;
  private BigDecimal projectedOpenPx;
  private LocalDateTime lastUpdateTime;
}
