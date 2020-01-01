package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.OrderCache;
import java.util.List;

/** Save order to redis server */
public interface OrderCacheManager {
  boolean processIfNewer(OrderCache cache);
  void setListeners(List<OrderCacheListener> listeners);
}
