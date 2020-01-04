package com.github.chaitriplez.openstreaming.service;

import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import java.time.Duration;
import java.util.List;

public interface JobService {
  Tuple<Job, List<JobDetail>> getJob(Long jobId);

  Tuple<Job, List<JobDetail>> waitJob(Long jobId, Duration timeout);

  Tuple<Job, List<JobDetail>> cancelJob(Long jobId);
}
