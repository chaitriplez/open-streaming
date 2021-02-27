package com.github.chaitriplez.openstreaming.service;

import com.github.chaitriplez.openstreaming.component.OrderCacheManager;
import com.github.chaitriplez.openstreaming.component.PushListener;
import com.github.chaitriplez.openstreaming.component.PushManager;
import com.github.chaitriplez.openstreaming.component.SymbolCacheManager;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.repository.BidOffer;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.repository.SymbolInfo;
import com.github.chaitriplez.openstreaming.util.OrderCacheConverter;
import com.github.chaitriplez.openstreaming.util.SymbolConverter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.settrade.openapi.protobuf.v1.BidOfferV1;
import com.settrade.openapi.protobuf.v1.InfoV1;
import com.settrade.openapi.protobuf.v1.OrderDerivV1;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Setter
@Slf4j
@Service
public class PushInfoServiceImpl implements PushInfoService {

  @Autowired private PushManager pushManager;
  @Autowired private OrderCacheManager orderCacheManager;
  @Autowired private SymbolCacheManager symbolCacheManager;
  @Autowired private OpenStreamingProperties osProp;

  @Override
  public void connect() throws Exception {
    pushManager.start();
  }

  @Override
  public void syncOrderCache() throws Exception {
    pushManager.subscribe(
        String.format("proto/ua/_broker/%s/_front/orderdvv1", osProp.getAccountNo()),
        new PushListener<OrderCache>() {
          @Override
          public OrderCache from(byte[] bytes) {
            try {
              OrderDerivV1 o = OrderDerivV1.parseFrom(bytes);
              return OrderCacheConverter.from(o);
            } catch (InvalidProtocolBufferException e) {
              throw new RuntimeException(e);
            }
          }

          @Override
          public void onMessage(OrderCache o) {
            orderCacheManager.processIfNewer(o);
          }
        });
  }

  @Override
  public void subscribeSymbol(String symbol) throws Exception {
    {
      log.info("Subscribe symbol info[{}]", symbol);
      String topic = String.format("proto/topic/infov1/%s", symbol);
      pushManager.unsubscribe(topic);
      pushManager.subscribe(
          topic,
          new PushListener<SymbolInfo>() {
            @Override
            public SymbolInfo from(byte[] bytes) {
              try {
                InfoV1 o = InfoV1.parseFrom(bytes);
                return SymbolConverter.from(o);
              } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
              }
            }

            @Override
            public void onMessage(SymbolInfo o) {
              symbolCacheManager.process(o);
            }
          });
    }

    {
      log.info("Subscribe bid/offer[{}]", symbol);
      String topic = String.format("proto/topic/bidofferv1/%s", symbol);
      pushManager.unsubscribe(topic);
      pushManager.subscribe(
          topic,
          new PushListener<BidOffer>() {
            @Override
            public BidOffer from(byte[] bytes) {
              try {
                BidOfferV1 o = BidOfferV1.parseFrom(bytes);
                return SymbolConverter.from(o);
              } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
              }
            }

            @Override
            public void onMessage(BidOffer o) {
              symbolCacheManager.process(o);
            }
          });
    }
  }
}
