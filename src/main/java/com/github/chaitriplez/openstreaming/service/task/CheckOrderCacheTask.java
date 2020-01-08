package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.repository.OrderCacheRepository;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class CheckOrderCacheTask extends AbstractOrderExecution<List<OrderCacheCondition>> {

  protected int retry = 2;
  protected Duration retryDelay = Duration.ofSeconds(1);

  public CheckOrderCacheTask(Long jobDetailId, List<OrderCacheCondition> request) {
    super(jobDetailId, request);
  }

  @Override
  public ExecutionResult execute() {
    OrderCacheRepository orderCacheRepository = context.getBean(OrderCacheRepository.class);

    for (OrderCacheCondition condition : request) {
      Optional<OrderCache> optional = orderCacheRepository.findById(condition.getOrderNo());
      if (!optional.isPresent()) {
        ExecutionResult result = new ExecutionResult();
        result.setStatus(ExecutionStatus.FAIL);
        result.setType(String.class.getCanonicalName());
        result.setResult("Precondition fail: order cache not found:" + condition.getOrderNo());
        return result;
      }

      boolean test = condition.getPredicate().test(optional.get());
      if (!test) {
        if (retry != 0) {
          retry--;
          ExecutionResult result = new ExecutionResult();
          result.setStatus(ExecutionStatus.RETRY);
          result.setRetryDelay(retryDelay);
          result.setType(String.class.getCanonicalName());
          result.setResult("Precondition fail: orderNo:" + condition.getOrderNo());
          return result;
        } else {
          ExecutionResult result = new ExecutionResult();
          result.setStatus(ExecutionStatus.FAIL);
          result.setType(String.class.getCanonicalName());
          result.setResult("Precondition fail: orderNo:" + condition.getOrderNo());
          return result;
        }
      }
    }
    ExecutionResult result = new ExecutionResult();
    result.setStatus(ExecutionStatus.SUCCESS);
    result.setType(String.class.getCanonicalName());
    result.setResult("Success");
    return result;
  }
}
