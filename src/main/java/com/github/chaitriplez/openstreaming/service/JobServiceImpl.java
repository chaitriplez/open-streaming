package com.github.chaitriplez.openstreaming.service;

import com.github.chaitriplez.openstreaming.component.JobManager;
import com.github.chaitriplez.openstreaming.component.OrderExecutionWorker;
import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import com.github.chaitriplez.openstreaming.repository.JobStatus;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Setter
@Service
public class JobServiceImpl implements JobService {

  @Autowired private JobManager jobManager;
  @Autowired private OrderExecutionWorker orderExecutionWorker;

  @Override
  public Tuple<Job, List<JobDetail>> getJob(Long jobId) {
    return jobManager.findJob(jobId).get();
  }

  @Override
  public Tuple<Job, List<JobDetail>> waitJob(Long jobId, Duration timeout) {
    try {
      Optional<Tuple<Job, List<JobDetail>>> optional = jobManager.waitAndFindJob(jobId, timeout);
      return optional.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return jobManager.findJob(jobId).get();
  }

  @Override
  public Tuple<Job, List<JobDetail>> cancelJob(Long jobId) {
    Optional<Tuple<Job, List<JobDetail>>> optional = jobManager.findJob(jobId);

    optional.ifPresent(
        tuple -> {
          if (tuple.getFirst().getStatus() != JobStatus.DONE) {
            tuple.getSecond().forEach(jobDetail -> orderExecutionWorker.cancel(jobDetail.getId()));
          }
        });

    return jobManager.findJob(jobId).get();
  }

  @Override
  public void cancelAllJobAndResetSequence() {
    orderExecutionWorker.cancelAll();
    jobManager.clearDatabaseAndResetSequence();
  }
}
