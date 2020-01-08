package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.component.AbstractOrderExecution;

public class EmptyTask extends AbstractOrderExecution<Void> {

  public EmptyTask(Long jobDetailId) {
    super(jobDetailId, null);
  }

  @Override
  public ExecutionResult execute() {
    ExecutionResult result = new ExecutionResult();
    result.setStatus(ExecutionStatus.SUCCESS);
    result.setType(String.class.getCanonicalName());
    result.setResult("Success");
    return result;
  }
}
