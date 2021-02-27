package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.component.SymbolCacheManager;
import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.repository.BidOffer;
import com.github.chaitriplez.openstreaming.repository.BidOfferRepository;
import com.github.chaitriplez.openstreaming.repository.SymbolInfo;
import com.github.chaitriplez.openstreaming.repository.SymbolInfoRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Setter
@RestController
public class MarketDataCacheController {

  @Autowired private SymbolInfoRepository symbolInfoRepository;

  @Autowired private BidOfferRepository bidOfferRepository;

  @Autowired private SymbolCacheManager symbolCacheManager;

  @GetMapping("/api-os/market-data-cache/v1/symbols")
  public List<Tuple<SymbolInfo, BidOffer>> listSymbols() {
    List<Tuple<SymbolInfo, BidOffer>> result = new ArrayList<>();
    symbolInfoRepository
        .findAll()
        .forEach(symbolInfo -> {
      BidOffer bidOffer = bidOfferRepository.findById(symbolInfo.getSymbol()).get();
      result.add(new Tuple<>(symbolInfo, bidOffer));
    });
    result.sort(Comparator.comparing(o -> o.getFirst().getSymbol()));
    return result;
  }

  @GetMapping("/api-os/market-data-cache/v1/symbols/{symbol}")
  public Tuple<SymbolInfo, BidOffer> getSymbol(@PathVariable("symbol") String symbol) {
    return new Tuple<>(symbolInfoRepository.findById(symbol).get(), bidOfferRepository.findById(symbol).get());
  }

  @PostMapping("/api-os/market-data-cache/v1/reset")
  public void reset() {
    symbolCacheManager.initial();
  }
}
