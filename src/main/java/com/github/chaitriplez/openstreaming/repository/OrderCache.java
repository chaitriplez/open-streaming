package com.github.chaitriplez.openstreaming.repository;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("order")
@Data
public class OrderCache {
  @Id private Long orderNo;
  @Indexed private String symbol;
  @Indexed private Boolean active;
  private String account;
  private String side;
  private String position;
  private BigDecimal px;
  private Integer qty;
  private Integer balanceQty;
  private Integer matchQty;
  private Integer cancelQty;
  private String status;
  private Long version;
}
