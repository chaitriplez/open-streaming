package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.BidOffer;
import com.github.chaitriplez.openstreaming.repository.SymbolInfo;

public interface SymbolCacheManager {
  void initial();
  void process(SymbolInfo info);
  void process(BidOffer bidOffer);
}
