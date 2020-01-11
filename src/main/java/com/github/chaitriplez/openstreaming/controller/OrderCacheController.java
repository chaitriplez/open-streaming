package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.component.OrderCacheManager;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.repository.OrderCacheRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Setter
@RestController
public class OrderCacheController {

  @Autowired private OrderCacheRepository orderCacheRepository;

  @Autowired private OrderCacheManager orderCacheManager;

  @GetMapping("/api-os/order-cache/v1/orders")
  public List<OrderCache> listOrder(
      @RequestParam(name = "symbol", required = false) String symbol,
      @RequestParam(name = "active", required = false) Boolean active) {
    List<OrderCache> result = new ArrayList<>();
    if (symbol != null && active != null) {
      orderCacheRepository.findBySymbolAndActive(symbol, active).forEach(result::add);
    } else if (symbol == null && active == null) {
      orderCacheRepository.findAll().forEach(result::add);
    } else if (symbol != null) {
      orderCacheRepository.findBySymbol(symbol).forEach(result::add);
    } else if (active != null) {
      orderCacheRepository.findByActive(active).forEach(result::add);
    }
    result.sort(Comparator.comparingLong(OrderCache::getOrderNo).reversed());
    return result;
  }

  @GetMapping("/api-os/order-cache/v1/orders/{orderNo}")
  public OrderCache getOrder(@PathVariable("orderNo") Long orderNo) {
    return orderCacheRepository.findById(orderNo).get();
  }

  @PostMapping("/api-os/order-cache/v1/reset")
  public void reset() {
    orderCacheManager.initial();
  }
}
