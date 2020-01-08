package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;
import com.github.chaitriplez.openstreaming.component.OrderExecutionContext;
import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.service.LimitOrderRequest;
import java.io.IOException;
import java.util.List;

public class QuotePlaceTask
    extends AbstractOrderExecution<Tuple<List<OrderCacheCondition>, LimitOrderRequest>> {

  protected final CheckOrderCacheTask precondition;
  protected final LimitOrderTask task;

  public QuotePlaceTask(
      Long jobDetailId, Tuple<List<OrderCacheCondition>, LimitOrderRequest> request) {
    super(jobDetailId, request);
    precondition = new CheckOrderCacheTask(jobDetailId, request.getFirst());
    task = new LimitOrderTask(jobDetailId, request.getSecond());
  }

  @Override
  public void setContext(OrderExecutionContext context) {
    super.setContext(context);
    precondition.setContext(context);
    task.setContext(context);
  }

  @Override
  public ExecutionResult execute() throws IOException {

    ExecutionResult checkResult = precondition.execute();
    if (checkResult.getStatus() != ExecutionStatus.SUCCESS) {
      return checkResult;
    }

    return task.execute();
  }
}
