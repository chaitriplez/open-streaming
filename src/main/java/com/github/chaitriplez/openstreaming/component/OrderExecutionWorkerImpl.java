package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.component.OrderExecution.ExecutionResult;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
public class OrderExecutionWorkerImpl implements OrderExecutionWorker, ApplicationContextAware {

  private static final Duration MIN_DELAY = Duration.ofMillis(100);

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private final ConcurrentMap<Long, Future> futures = new ConcurrentHashMap<>();
  private ApplicationContext applicationContext;
  private final OrderExecutionContext context = clazz -> applicationContext.getBean(clazz);

  @Autowired private JobManager jobManager;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void submit(OrderExecution exe) {
    Future f = executor.submit(new Task(exe));
    futures.put(exe.jobDetailId(), f);
  }

  @Override
  public boolean cancel(Long jobDetailId) {
    Future f = futures.get(jobDetailId);
    if (f != null && f.cancel(false)) {
      jobManager.cancelJobDetail(jobDetailId);
      futures.remove(jobDetailId);
      return true;
    }
    return false;
  }

  private class Task implements Runnable {

    private final OrderExecution exe;

    public Task(OrderExecution exe) {
      this.exe = exe;
    }

    @Override
    public void run() {
      exe.setContext(context);
      try {
        jobManager.startJobDetail(exe.jobDetailId());

        ExecutionResult result = exe.execute();

        switch (result.getStatus()) {
          case SUCCESS:
            jobManager.successJobDetail(exe.jobDetailId(), result.getType(), result.getResult());
            futures.remove(exe.jobDetailId());
            break;
          case FAIL:
            jobManager.failJobDetail(exe.jobDetailId(), result.getType(), result.getResult());
            futures.remove(exe.jobDetailId());
            break;
          case RETRY:
            jobManager.retryJobDetail(exe.jobDetailId(), result.getType(), result.getResult());
            Future f = executor.schedule(
                new Task(exe),
                MIN_DELAY.toMillis() + result.getRetryDelay().toMillis(),
                TimeUnit.MILLISECONDS);
            futures.replace(exe.jobDetailId(), f);
            break;
        }
      } catch (Exception e) {
        log.error("Execution error", e);
        jobManager.failJobDetail(
            exe.jobDetailId(), String.class.getCanonicalName(), e.getMessage());
        futures.remove(exe.jobDetailId());
      } finally {
        exe.setContext(null);
      }
    }
  }
}
