package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.api.OrderResponse;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorQueryAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepQueryAPI;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties.UserType;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.util.OrderCacheConverter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@Setter
@EnableConfigurationProperties(PullOrderProperties.class)
@Component
public class PullOrderWorkerImpl implements PullOrderWorker, OrderCacheListener {

  private final ConcurrentMap<Long, ScheduledFuture> monitorOrders = new ConcurrentHashMap<>();
  private final ScheduledExecutorService executorService =
      Executors.newSingleThreadScheduledExecutor();

  @Autowired private PullOrderProperties pullOrderProp;
  @Autowired private OpenStreamingProperties osProp;
  @Autowired private OrderCacheManager orderCacheManager;

  @Autowired(required = false)
  private SettradeDerivativesInvestorQueryAPI investorQueryAPI;

  @Autowired(required = false)
  private SettradeDerivativesMktRepQueryAPI mktRepQueryAPI;

  @PostConstruct
  public void init() {
    log.info("Start pull order worker: interval[{}]", pullOrderProp.getCheckInterval());
  }

  @PreDestroy
  public void destroy() {
    log.info("Stop pull order worker");
    executorService.shutdownNow();
    monitorOrders.clear();
  }

  @Override
  public void onChange(OrderCache cache) {
    if (cache.getBalanceQty() != 0) {
      monitor(cache.getOrderNo());
    } else {
      stopMonitor(cache.getOrderNo());
    }
  }

  @Override
  public void monitor(Long orderNo) {
    monitorOrders.computeIfAbsent(
        orderNo,
        key -> {
          log.info("Start monitor orderNo {}", orderNo);
          return executorService.scheduleWithFixedDelay(
              new Task(orderNo),
              pullOrderProp.getCheckInterval().toMillis(),
              pullOrderProp.getCheckInterval().toMillis(),
              TimeUnit.MILLISECONDS);
        });
  }

  @Override
  public void stopMonitor(Long orderNo) {
    monitorOrders.computeIfPresent(
        orderNo,
        (lOrderNo, scheduledFuture) -> {
          log.info("Stop monitor orderNo {}", orderNo);
          scheduledFuture.cancel(false);
          return null;
        });
  }

  private class Task implements Runnable {

    private Long orderNo;

    public Task(Long orderNo) {
      this.orderNo = orderNo;
    }

    @Override
    public void run() {
      Call<OrderResponse> call = getOrder(orderNo);
      Response<OrderResponse> response;
      try {
        response = call.execute();
      } catch (IOException e) {
        log.warn("Cannot get order:{}", orderNo, e);
        return;
      }
      if (!response.isSuccessful()) {
        log.warn("Cannot get order:{} cause:{}", orderNo, response.code());
        if (response.code() == HttpStatus.UNAUTHORIZED.value()
            || response.code() == HttpStatus.FORBIDDEN.value()) {
          log.info("Unauthorized/Forbidden stop monitor:{}", orderNo);
          stopMonitor(orderNo);
        }
        return;
      }
      OrderResponse orderResponse = response.body();
      OrderCache cache = OrderCacheConverter.from(orderResponse);
      orderCacheManager.processIfNewer(cache);
    }

    private Call<OrderResponse> getOrder(Long orderNo) {
      if (osProp.getUserType() == UserType.INVESTOR) {
        return investorQueryAPI.getOrder(osProp.getBrokerId(), osProp.getAccountNo(), orderNo);
      } else {
        return mktRepQueryAPI.getOrder(osProp.getBrokerId(), orderNo);
      }
    }
  }
}
