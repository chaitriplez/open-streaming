package com.github.chaitriplez.openstreaming.util;

import com.github.chaitriplez.openstreaming.api.OrderResponse;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.google.type.Money;
import com.settrade.openapi.protobuf.v1.OrderDerivV1;
import java.math.BigDecimal;

public class OrderCacheConverter {

  public static OrderCache from(OrderResponse o) {
    OrderCache cache = new OrderCache();
    cache.setOrderNo(o.getOrderNo());
    cache.setSymbol(o.getSymbol());
    cache.setActive(o.getBalanceQty() != 0);
    cache.setAccount(o.getAccountNo());
    cache.setSide(o.getSide().toUpperCase());
    cache.setPosition(o.getPosition().toUpperCase());
    cache.setPx(o.getPrice());
    cache.setQty(o.getQty());
    cache.setBalanceQty(o.getBalanceQty());
    cache.setMatchQty(o.getMatchQty());
    cache.setCancelQty(o.getCancelQty());
    cache.setStatus(o.getStatus());
    cache.setVersion(o.getVersion());
    return cache;
  }

  public static OrderCache from(OrderDerivV1 o) {
    OrderCache cache = new OrderCache();
    cache.setOrderNo(Long.parseLong(o.getOrderNo()));
    cache.setSymbol(o.getSeriesId());
    cache.setActive(o.getBalanceVolume() != 0);
    cache.setAccount(o.getAccountNo());
    cache.setSide(o.getSide().toString());
    cache.setPosition(o.getPosition().toString());
    cache.setPx(convert(o.getPrice()));
    cache.setQty((int) o.getVolume());
    cache.setBalanceQty((int) o.getBalanceVolume());
    cache.setMatchQty((int) o.getMatchedVolume());
    cache.setCancelQty((int) o.getCancelledVolume());
    cache.setStatus(o.getStatus());
    cache.setVersion((long) o.getVersion());
    return cache;
  }

  private static BigDecimal convert(Money money) {
    return BigDecimal.valueOf(money.getNanos())
        .movePointLeft(9)
        .add(BigDecimal.valueOf(money.getUnits()))
        .stripTrailingZeros();
  }
}
