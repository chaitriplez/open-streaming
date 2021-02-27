package com.github.chaitriplez.openstreaming.util;

import com.github.chaitriplez.openstreaming.repository.BidOffer;
import com.github.chaitriplez.openstreaming.repository.SymbolInfo;
import com.google.type.Money;
import com.settrade.openapi.protobuf.v1.BidOfferV1;
import com.settrade.openapi.protobuf.v1.InfoV1;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SymbolConverter {
  public static SymbolInfo from(InfoV1 o) {
    SymbolInfo info = new SymbolInfo();
    info.setSymbol(o.getSymbol());
    info.setHighPx(convert(o.getHigh()));
    info.setLowPx(convert(o.getLow()));
    info.setLastPx(convert(o.getLast()));
    info.setProjectedOpenPx(convert(o.getProjectedOpenPrice()));
    info.setTotalQty(o.getTotalVolume());
    info.setLastUpdateTime(LocalDateTime.now());
    return info;
  }

  public static BidOffer from(BidOfferV1 o) {
    BidOffer bidOffer = new BidOffer();

    bidOffer.setSymbol(o.getSymbol());

    bidOffer.setBidPx1(convert(o.getBidPrice1()));
    bidOffer.setBidPx2(convert(o.getBidPrice2()));
    bidOffer.setBidPx3(convert(o.getBidPrice3()));
    bidOffer.setBidPx4(convert(o.getBidPrice4()));
    bidOffer.setBidPx5(convert(o.getBidPrice4()));

    bidOffer.setOfferPx1(convert(o.getAskPrice1()));
    bidOffer.setOfferPx2(convert(o.getAskPrice2()));
    bidOffer.setOfferPx3(convert(o.getAskPrice3()));
    bidOffer.setOfferPx4(convert(o.getAskPrice4()));
    bidOffer.setOfferPx5(convert(o.getAskPrice5()));

    bidOffer.setBidQty1(o.getBidVolume1());
    bidOffer.setBidQty2(o.getBidVolume2());
    bidOffer.setBidQty3(o.getBidVolume3());
    bidOffer.setBidQty4(o.getBidVolume4());
    bidOffer.setBidQty5(o.getBidVolume5());

    bidOffer.setOfferQty1(o.getAskVolume1());
    bidOffer.setOfferQty2(o.getAskVolume2());
    bidOffer.setOfferQty3(o.getAskVolume3());
    bidOffer.setOfferQty4(o.getAskVolume4());
    bidOffer.setOfferQty5(o.getAskVolume5());

    bidOffer.setLastUpdateTime(LocalDateTime.now());

    return bidOffer;
  }

  private static BigDecimal convert(Money money) {
    return BigDecimal.valueOf(money.getNanos())
        .movePointLeft(9)
        .add(BigDecimal.valueOf(money.getUnits()))
        .stripTrailingZeros();
  }
}
