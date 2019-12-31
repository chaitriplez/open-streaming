package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.OrderCache;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
public class OrderCacheLoggerListener implements OrderCacheListener {

  @Override
  public void onChange(OrderCache cache) {
    log.info(
        "Order:{} {} {}@{} qty:{} balance:{} match:{} cancel:{} status:{}",
        cache.getOrderNo(),
        cache.getSide(),
        cache.getSymbol(),
        cache.getPx(),
        cache.getQty(),
        cache.getBalanceQty(),
        cache.getMatchQty(),
        cache.getCancelQty(),
        cache.getStatus());
  }
}
