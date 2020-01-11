package com.github.chaitriplez.openstreaming.component;

public interface OrderExecutionWorker {
  void submit(OrderExecution execution);

  boolean cancel(Long jobDetailId);

  void cancelAll();
}
