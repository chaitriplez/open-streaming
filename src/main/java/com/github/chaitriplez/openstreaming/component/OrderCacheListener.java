package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.OrderCache;

public interface OrderCacheListener {

  void onChange(OrderCache cache);
}
