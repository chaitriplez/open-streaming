package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.repository.OrderCacheRepository;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
public class OrderCacheManagerImpl implements OrderCacheManager {

  private final ConcurrentMap<Long, Long> orderVersions = new ConcurrentHashMap<>();

  private List<OrderCacheListener> listeners;

  @Autowired private OrderCacheRepository orderCacheRepository;

  @Override
  public boolean processIfNewer(OrderCache cache) {
    final Long key = cache.getOrderNo();
    final Long newValue = cache.getVersion();

    Long oldValue;
    do {
      oldValue = orderVersions.putIfAbsent(key, newValue);
    } while ((oldValue != null && oldValue < newValue)
        && !orderVersions.replace(key, oldValue, newValue));
    if (oldValue == null || (oldValue != null && oldValue < newValue)) {
      orderCacheRepository.save(cache);
      if (listeners != null) {
        listeners.forEach(listener -> listener.onChange(cache));
      }
      return true;
    }
    return false;
  }

  @Autowired(required = false)
  @Override
  public void setListeners(List<OrderCacheListener> listeners) {
    this.listeners = listeners;
  }
}
