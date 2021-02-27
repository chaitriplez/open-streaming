package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.api.InvestorChangeOrderRequest;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorOrderAPI;
import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.service.ChangePxQtyRequest;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public class ChangePxQtyTask extends AbstractOrderExecution<ChangePxQtyRequest> {

  public ChangePxQtyTask(Long jobDetailId, ChangePxQtyRequest request) {
    super(jobDetailId, request);
  }

  @Override
  public ExecutionResult execute() throws IOException {
    Call<Void> call = changeOrder();

    Response<Void> response = call.execute();
    if (!response.isSuccessful()) {
      ExecutionResult result = new ExecutionResult();
      result.setStatus(ExecutionStatus.FAIL);
      result.setType(String.class.getCanonicalName());
      result.setResult(response.errorBody().string());
      return result;
    }

    ExecutionResult result = new ExecutionResult();
    result.setStatus(ExecutionStatus.SUCCESS);
    result.setType(String.class.getCanonicalName());
    result.setResult("Request summit.");
    return result;
  }

  private Call<Void> changeOrder() {
    OpenStreamingProperties osProp = context.getBean(OpenStreamingProperties.class);
    SettradeDerivativesInvestorOrderAPI api =
        context.getBean(SettradeDerivativesInvestorOrderAPI.class);
    InvestorChangeOrderRequest.InvestorChangeOrderRequestBuilder builder =
        InvestorChangeOrderRequest.builder().pin(osProp.getPin());
    if (request.isChangePx()) {
      builder.newPrice(request.getPx());
    }
    if (request.isChangeQty()) {
      builder.newVolume(request.getQty());
    }
    return api.changeOrder(
        osProp.getBrokerId(), osProp.getAccountNo(), request.getOrderNo(), builder.build());
  }
}
