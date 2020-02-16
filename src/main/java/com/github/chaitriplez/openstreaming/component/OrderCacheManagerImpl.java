package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.api.OrderResponse;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorQueryAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepQueryAPI;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties.UserType;
import com.github.chaitriplez.openstreaming.controller.CallFailException;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.repository.OrderCacheRepository;
import com.github.chaitriplez.openstreaming.util.OpenStreamingConstants;
import com.github.chaitriplez.openstreaming.util.OrderCacheConverter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@Setter
@Component
public class OrderCacheManagerImpl implements OrderCacheManager {

  private final ConcurrentMap<Long, Long> orderVersions = new ConcurrentHashMap<>();
  private List<OrderCacheListener> listeners;

  @Autowired private OpenStreamingProperties osProp;

  @Autowired private OrderCacheRepository orderCacheRepository;

  @Autowired(required = false)
  private SettradeDerivativesInvestorQueryAPI investorQueryAPI;

  @Autowired(required = false)
  private SettradeDerivativesMktRepQueryAPI mktRepQueryAPI;

  @Override
  public void initial() {
    log.info("Remove all order cache...");
    orderCacheRepository.deleteAll();
    orderVersions.clear();
    log.info("List all order from remote server...");
    Call<List<OrderResponse>> call = listAllOrder();
    Response<List<OrderResponse>> response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      throw new CallFailException(
          "Cannot execute remote call",
          e,
          HttpStatus.SERVICE_UNAVAILABLE,
          OpenStreamingConstants.REMOTE_CALL_ERROR_CODE);
    }
    if (response.isSuccessful()) {
      response
          .body()
          .forEach(orderResponse -> processIfNewer(OrderCacheConverter.from(orderResponse)));
    } else {
      log.warn("Cannot list order {}", response);
      throw new CallFailException(
          "Cannot list order",
          HttpStatus.valueOf(response.code()),
          OpenStreamingConstants.REMOTE_CALL_ERROR_CODE);
    }
    log.info("Initial order cache completed.");
  }

  @Override
  public boolean processIfNewer(OrderCache cache) {
    final Long key = cache.getOrderNo();
    final Long newValue = cache.getVersion();

    Long oldValue;
    do {
      oldValue = orderVersions.putIfAbsent(key, newValue);
    } while ((oldValue != null && oldValue < newValue)
        && !orderVersions.replace(key, oldValue, newValue));
    if (oldValue == null || (oldValue != null && oldValue < newValue)) {
      orderCacheRepository.save(cache);
      if (listeners != null) {
        listeners.forEach(listener -> listener.onChange(cache));
      }
      return true;
    }
    return false;
  }

  @Autowired(required = false)
  @Override
  public void setListeners(List<OrderCacheListener> listeners) {
    this.listeners = listeners;
  }

  private Call<List<OrderResponse>> listAllOrder() {
    if (osProp.getUserType() == UserType.INVESTOR) {
      return investorQueryAPI.listOrder(osProp.getBrokerId(), osProp.getAccountNo());
    } else {
      return mktRepQueryAPI.listOrder(osProp.getBrokerId(), osProp.getAccountNo());
    }
  }
}
