package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface JobManager {

  Optional<Tuple<Job, List<JobDetail>>> findJob(Long jobId);

  Optional<Tuple<Job, List<JobDetail>>> waitAndFindJob(Long jobId, Duration timeout)
      throws InterruptedException;

  Tuple<Job, List<JobDetail>> prepareJob(int numberOfJobDetail);

  void save(Tuple<Job, List<JobDetail>> tuple);

  void startJobDetail(Long jobDetailId);

  void cancelJobDetail(Long jobDetailId);

  void successJobDetail(Long jobDetailId, String canonicalName, String response);

  void failJobDetail(Long jobDetailId, String canonicalName, String response);

  void retryJobDetail(Long jobDetailId, String canonicalName, String response);

  void clearDatabaseAndResetSequence();
}
