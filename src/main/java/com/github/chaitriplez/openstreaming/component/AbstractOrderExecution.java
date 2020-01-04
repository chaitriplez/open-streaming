package com.github.chaitriplez.openstreaming.component;

public abstract class AbstractOrderExecution<T> implements OrderExecution {

  protected final Long jobDetailId;
  protected final T request;
  protected OrderExecutionContext context;

  public AbstractOrderExecution(Long jobDetailId, T request) {
    this.jobDetailId = jobDetailId;
    this.request = request;
  }

  @Override
  public final void setContext(OrderExecutionContext context) {
    this.context = context;
  }

  @Override
  public final Long jobDetailId() {
    return jobDetailId;
  }

  @Override
  public abstract ExecutionResult execute() throws Exception;
}
