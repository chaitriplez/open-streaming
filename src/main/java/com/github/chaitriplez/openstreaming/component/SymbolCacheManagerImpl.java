package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.BidOffer;
import com.github.chaitriplez.openstreaming.repository.BidOfferRepository;
import com.github.chaitriplez.openstreaming.repository.SymbolInfo;
import com.github.chaitriplez.openstreaming.repository.SymbolInfoRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
public class SymbolCacheManagerImpl implements SymbolCacheManager {

  @Autowired private SymbolInfoRepository symbolInfoRepository;
  @Autowired private BidOfferRepository bidOfferRepository;

  @Override
  public void initial() {
    log.info("Clear all market data...");
    symbolInfoRepository.deleteAll();
    bidOfferRepository.deleteAll();
  }

  @Override
  public void process(SymbolInfo info) {
    symbolInfoRepository.save(info);
  }

  @Override
  public void process(BidOffer bidOffer) {
    bidOfferRepository.save(bidOffer);
  }
}
