package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.component.OrderExecution.ExecutionResult;
import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import com.github.chaitriplez.openstreaming.repository.JobDetailRepository;
import com.github.chaitriplez.openstreaming.repository.JobRepository;
import com.github.chaitriplez.openstreaming.repository.JobStatus;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
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

  private ApplicationContext applicationContext;

  @Autowired private JobRepository jobRepository;

  @Autowired private JobDetailRepository jobDetailRepository;

  @Autowired private JobLatch jobLatch;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void submit(OrderExecution exe) {
    executor.submit(new Task(exe));
  }

  private void jobProcessing(String jobDetailId) {
    JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();
    jobDetail.setStatus(JobStatus.PROCESSING);
    jobDetailRepository.save(jobDetail);
  }

  private void jobDone(String jobDetailId, String result) {
    JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();
    jobDetail.setStatus(JobStatus.DONE);
    jobDetail.setResult(result);
    jobDetailRepository.save(jobDetail);
    CountDownLatch latch = jobLatch.get(jobDetail.getJobId());
    latch.countDown();
    if (latch.getCount() == 0) {
      Job job = jobRepository.findById(jobDetail.getJobId()).get();
      job.setStatus(JobStatus.DONE);
      jobRepository.save(job);
    }
  }

  private class Task implements Runnable {

    private final OrderExecution exe;

    public Task(OrderExecution exe) {
      this.exe = exe;
    }

    @Override
    public void run() {
      exe.setContext(new Context());
      try {
        jobProcessing(exe.jobDetailId());

        ExecutionResult result = exe.execute();

        switch (result.getStatus()) {
          case SUCCESS:
          case FAIL:
            jobDone(exe.jobDetailId(), result.getResult());
            break;
          case RETRY:
            Duration delay = MIN_DELAY.plus(result.getRetryDelay());
            executor.schedule(new Task(exe), delay.toMillis(), TimeUnit.MILLISECONDS);
            break;
        }
      } catch (Exception e) {
        log.error("Execution error", e);
        jobDone(exe.jobDetailId(), e.getMessage());
      } finally {
        exe.setContext(null);
      }
    }
  }

  private class Context implements OrderExecutionContext {
    @Override
    public <T> T getBean(Class<T> requiredType) {
      return applicationContext.getBean(requiredType);
    }
  }
}
