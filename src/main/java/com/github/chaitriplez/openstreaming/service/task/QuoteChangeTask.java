package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;
import com.github.chaitriplez.openstreaming.component.OrderExecutionContext;
import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.service.ChangePxQtyRequest;
import java.io.IOException;
import java.util.List;

public class QuoteChangeTask
    extends AbstractOrderExecution<Tuple<List<OrderCacheCondition>, ChangePxQtyRequest>> {

  protected final CheckOrderCacheTask precondition;
  protected final ChangePxQtyTask task;

  public QuoteChangeTask(
      Long jobDetailId, Tuple<List<OrderCacheCondition>, ChangePxQtyRequest> request) {
    super(jobDetailId, request);
    precondition = new CheckOrderCacheTask(jobDetailId, request.getFirst());
    task = new ChangePxQtyTask(jobDetailId, request.getSecond());
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
