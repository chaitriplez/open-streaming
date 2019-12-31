package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.OrderCache;

/** Save order to redis server */
public interface OrderCacheManager {
  boolean processIfNewer(OrderCache cache);
}
