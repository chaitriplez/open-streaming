package com.github.chaitriplez.openstreaming.service;

import com.github.chaitriplez.openstreaming.component.OrderCacheManager;
import com.github.chaitriplez.openstreaming.component.PushListener;
import com.github.chaitriplez.openstreaming.component.PushManager;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.util.OrderCacheConverter;
import com.google.protobuf.InvalidProtocolBufferException;
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
}
