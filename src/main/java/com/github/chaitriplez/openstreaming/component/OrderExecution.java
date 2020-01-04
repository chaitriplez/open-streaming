package com.github.chaitriplez.openstreaming.component;

import java.time.Duration;
import lombok.Data;

public interface OrderExecution {

  /** @param context inject before execute and remove after execute */
  void setContext(OrderExecutionContext context);

  Long jobDetailId();

  ExecutionResult execute() throws Exception;

  enum ExecutionStatus {
    SUCCESS,
    FAIL,
    RETRY
  }

  @Data
  class ExecutionResult {
    private ExecutionStatus status;
    private String type;
    private String result;
    private Duration retryDelay;
  }
}
