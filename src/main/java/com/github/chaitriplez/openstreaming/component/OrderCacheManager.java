package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.OrderCache;

public interface OrderCacheManager {
  boolean processIfNewer(OrderCache cache);
}
