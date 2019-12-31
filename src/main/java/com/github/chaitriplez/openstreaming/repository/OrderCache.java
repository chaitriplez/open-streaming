package com.github.chaitriplez.openstreaming.repository;

import com.github.chaitriplez.openstreaming.api.OrderResponse;
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

  public static OrderCache from(OrderResponse o) {
    OrderCache cache = new OrderCache();
    cache.setOrderNo(o.getOrderNo());
    cache.setSymbol(o.getSymbol());
    cache.setActive(o.getBalanceQty() != 0);
    cache.setAccount(o.getAccountNo());
    cache.setSide(o.getSide());
    cache.setPosition(o.getPosition());
    cache.setPx(o.getPrice());
    cache.setQty(o.getQty());
    cache.setBalanceQty(o.getBalanceQty());
    cache.setMatchQty(o.getMatchQty());
    cache.setCancelQty(o.getCancelQty());
    cache.setStatus(o.getStatus());
    cache.setVersion(o.getVersion());
    return cache;
  }
}
