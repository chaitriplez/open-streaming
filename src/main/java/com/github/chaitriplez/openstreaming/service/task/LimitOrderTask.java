package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.api.InvestorPlaceOrderRequest;
import com.github.chaitriplez.openstreaming.api.PlaceOrderResponse;
import com.github.chaitriplez.openstreaming.api.PriceType;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorOrderAPI;
import com.github.chaitriplez.openstreaming.api.ValidityType;
import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;
import com.github.chaitriplez.openstreaming.component.OrderCacheManager;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.service.LimitOrderRequest;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public class LimitOrderTask extends AbstractOrderExecution<LimitOrderRequest> {

  public LimitOrderTask(Long jobDetailId, LimitOrderRequest request) {
    super(jobDetailId, request);
  }

  @Override
  public ExecutionResult execute() throws IOException {
    Call<PlaceOrderResponse> call = placeOrder();

    Response<PlaceOrderResponse> response = call.execute();
    if (!response.isSuccessful()) {
      ExecutionResult result = new ExecutionResult();
      result.setStatus(ExecutionStatus.FAIL);
      result.setType(String.class.getCanonicalName());
      result.setResult(response.errorBody().string());
      return result;
    }
    PlaceOrderResponse orderResponse = response.body();
    Long orderNo = orderResponse.getOrderNo();
    initOrderCache(orderNo);

    ExecutionResult result = new ExecutionResult();
    result.setStatus(ExecutionStatus.SUCCESS);
    result.setType(Long.class.getCanonicalName());
    result.setResult(orderResponse.getOrderNo().toString());
    return result;
  }

  private Call<PlaceOrderResponse> placeOrder() {
    OpenStreamingProperties osProp = context.getBean(OpenStreamingProperties.class);
    SettradeDerivativesInvestorOrderAPI api =
        context.getBean(SettradeDerivativesInvestorOrderAPI.class);
    InvestorPlaceOrderRequest place =
        InvestorPlaceOrderRequest.builder()
            .pin(osProp.getPin())
            .position(request.getPosition())
            .side(request.getSide())
            .symbol(request.getSymbol())
            .volume(request.getQty())
            .priceType(PriceType.LIMIT)
            .price(request.getPx())
            .validityType(ValidityType.GOOD_TILL_DAY)
            .bypassWarning(true)
            .build();
    return api.placeOrder(osProp.getBrokerId(), osProp.getAccountNo(), place);
  }

  private void initOrderCache(Long orderNo) {
    OpenStreamingProperties osProp = context.getBean(OpenStreamingProperties.class);
    OrderCacheManager orderCacheManager = context.getBean(OrderCacheManager.class);
    OrderCache cache = new OrderCache();
    cache.setOrderNo(orderNo);
    cache.setSymbol(request.getSymbol());
    cache.setActive(true);
    cache.setAccount(osProp.getAccountNo());
    cache.setSide(request.getSide().toString());
    cache.setPosition(request.getPosition().toString());
    cache.setPx(request.getPx());
    cache.setQty(request.getQty());
    cache.setBalanceQty(request.getQty());
    cache.setMatchQty(0);
    cache.setCancelQty(0);
    cache.setStatus("-");
    cache.setVersion(-1L);
    orderCacheManager.processIfNewer(cache);
  }
}
