package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.api.InvestorCancelOrderRequest;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepOrderAPI;
import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties.UserType;
import java.io.IOException;
import java.time.Duration;
import retrofit2.Call;
import retrofit2.Response;

/** Cancel order: retry 2 times, delay 1 second. */
public class CancelOrderTask extends AbstractOrderExecution<Long> {

  protected int retry = 2;
  protected Duration retryDelay = Duration.ofSeconds(1);

  public CancelOrderTask(Long jobDetailId, Long request) {
    super(jobDetailId, request);
  }

  @Override
  public ExecutionResult execute() throws IOException {
    Call<Void> call = cancelOrder();

    Response<Void> response = call.execute();
    if (!response.isSuccessful()) {
      if (retry != 0) {
        retry--;
        ExecutionResult result = new ExecutionResult();
        result.setStatus(ExecutionStatus.RETRY);
        result.setRetryDelay(retryDelay);
        result.setType(String.class.getCanonicalName());
        result.setResult(response.errorBody().string());
        return result;
      } else {
        ExecutionResult result = new ExecutionResult();
        result.setStatus(ExecutionStatus.FAIL);
        result.setType(String.class.getCanonicalName());
        result.setResult(response.errorBody().string());
        return result;
      }
    }

    ExecutionResult result = new ExecutionResult();
    result.setStatus(ExecutionStatus.SUCCESS);
    result.setType(String.class.getCanonicalName());
    result.setResult("Cancel success");
    return result;
  }

  private Call<Void> cancelOrder() {
    OpenStreamingProperties osProp = context.getBean(OpenStreamingProperties.class);
    if (osProp.getUserType() == UserType.INVESTOR) {
      SettradeDerivativesInvestorOrderAPI api =
          context.getBean(SettradeDerivativesInvestorOrderAPI.class);
      InvestorCancelOrderRequest cancel =
          InvestorCancelOrderRequest.builder().pin(osProp.getPin()).build();
      return api.cancelOrder(osProp.getBrokerId(), osProp.getAccountNo(), request, cancel);
    } else {
      SettradeDerivativesMktRepOrderAPI api =
          context.getBean(SettradeDerivativesMktRepOrderAPI.class);
      return api.cancelOrder(osProp.getBrokerId(), osProp.getAccountNo(), request);
    }
  }
}
