package com.github.chaitriplez.openstreaming.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("bidOffer")
@Data
public class BidOffer {
  @Id private String symbol;
  private BigDecimal bidPx1;
  private BigDecimal bidPx2;
  private BigDecimal bidPx3;
  private BigDecimal bidPx4;
  private BigDecimal bidPx5;

  private Long bidQty1;
  private Long bidQty2;
  private Long bidQty3;
  private Long bidQty4;
  private Long bidQty5;

  private BigDecimal offerPx1;
  private BigDecimal offerPx2;
  private BigDecimal offerPx3;
  private BigDecimal offerPx4;
  private BigDecimal offerPx5;

  private Long offerQty1;
  private Long offerQty2;
  private Long offerQty3;
  private Long offerQty4;
  private Long offerQty5;

  private LocalDateTime lastUpdateTime;
}
