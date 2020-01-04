package com.github.chaitriplez.openstreaming.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chaitriplez.openstreaming.api.InvestorPlaceOrderRequest;
import com.github.chaitriplez.openstreaming.api.PlaceOrderRequest;
import com.github.chaitriplez.openstreaming.api.PlaceOrderResponse;
import com.github.chaitriplez.openstreaming.api.PriceType;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepOrderAPI;
import com.github.chaitriplez.openstreaming.api.ValidityType;
import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;
import com.github.chaitriplez.openstreaming.component.PullOrderWorker;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties.UserType;
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
    PullOrderWorker pullOrderWorker = context.getBean(PullOrderWorker.class);
    ObjectMapper mapper = context.getBean(ObjectMapper.class);

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
    pullOrderWorker.monitor(orderNo);

    ExecutionResult result = new ExecutionResult();
    result.setStatus(ExecutionStatus.SUCCESS);
    result.setType(PlaceOrderResponse.class.getCanonicalName());
    result.setResult(mapper.writeValueAsString(orderResponse));
    return result;
  }

  private Call<PlaceOrderResponse> placeOrder() {
    OpenStreamingProperties osProp = context.getBean(OpenStreamingProperties.class);
    if (osProp.getUserType() == UserType.INVESTOR) {
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
    } else {
      SettradeDerivativesMktRepOrderAPI api =
          context.getBean(SettradeDerivativesMktRepOrderAPI.class);
      PlaceOrderRequest place =
          PlaceOrderRequest.builder()
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
  }
}
