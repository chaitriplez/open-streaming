package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import com.github.chaitriplez.openstreaming.repository.JobDetailRepository;
import com.github.chaitriplez.openstreaming.repository.JobDetailStatus;
import com.github.chaitriplez.openstreaming.repository.JobRepository;
import com.github.chaitriplez.openstreaming.repository.JobStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
public class JobManagerImpl implements JobManager {

  private final AtomicLong jobIdGenerator = new AtomicLong();
  private final AtomicLong jobDetailIdGenerator = new AtomicLong();

  @Autowired private JobRepository jobRepository;

  @Autowired private JobDetailRepository jobDetailRepository;

  @Autowired private JobLatch jobLatch;

  @PostConstruct
  public void init() {
    jobRepository
        .findAll()
        .forEach(
            job -> {
              if ((job.getId()) > jobIdGenerator.get()) {
                jobIdGenerator.set(job.getId());
              }
            });

    jobDetailRepository
        .findByJobId(jobIdGenerator.get())
        .forEach(
            detail -> {
              if ((detail.getId()) > jobDetailIdGenerator.get()) {
                jobDetailIdGenerator.set(detail.getId());
              }
            });
    log.info("Initial Job Id: {}", jobIdGenerator.incrementAndGet());
    log.info("Initial Job Detail Id: {}", jobDetailIdGenerator.incrementAndGet());
  }

  @Override
  public Optional<Tuple<Job, List<JobDetail>>> findJob(Long jobId) {
    return doFind(jobId);
  }

  @Override
  public Optional<Tuple<Job, List<JobDetail>>> waitAndFindJob(Long jobId, Duration timeout)
      throws InterruptedException {
    jobLatch.get(jobId).await(timeout.toMillis(), TimeUnit.MILLISECONDS);
    return doFind(jobId);
  }

  @Override
  public Tuple<Job, List<JobDetail>> prepareJob(int numberOfJobDetail) {
    LocalDateTime now = LocalDateTime.now();
    Job job = new Job();
    job.setId(jobIdGenerator.getAndIncrement());
    job.setNumberOfJobDetail(numberOfJobDetail);
    job.setStatus(JobStatus.NEW);
    job.setCreateTime(now);
    job.setLastUpdateTime(now);
    job.calculateTotalTime();

    List<JobDetail> details = new ArrayList<>();
    for (int i = 0; i < numberOfJobDetail; i++) {
      JobDetail detail = new JobDetail();
      detail.setId(jobDetailIdGenerator.getAndIncrement());
      detail.setJobId(job.getId());
      detail.setStatus(JobDetailStatus.NEW);
      detail.setLastUpdateTime(now);
      detail.calculateTotalTime();
      details.add(detail);
    }

    return new Tuple<>(job, details);
  }

  @Override
  public void save(Tuple<Job, List<JobDetail>> tuple) {
    jobRepository.save(tuple.getFirst());
    jobDetailRepository.saveAll(tuple.getSecond());
    jobLatch.create(tuple.getFirst().getId(), tuple.getSecond().size());
  }

  @Override
  public void startJobDetail(Long jobDetailId) {
    LocalDateTime now = LocalDateTime.now();
    JobDetail detail = jobDetailRepository.findById(jobDetailId).get();
    detail.setExecutionTime(now);
    detail.setLastUpdateTime(now);
    detail.calculateTotalTime();
    detail.setStatus(JobDetailStatus.PROCESSING);
    jobDetailRepository.save(detail);
  }

  @Override
  public void cancelJobDetail(Long jobDetailId) {
    LocalDateTime now = LocalDateTime.now();
    JobDetail detail = jobDetailRepository.findById(jobDetailId).get();
    detail.setLastUpdateTime(now);
    detail.calculateTotalTime();
    detail.setStatus(JobDetailStatus.CANCELLED);
    jobDetailRepository.save(detail);

    releaseJob(detail.getJobId());
  }

  @Override
  public void successJobDetail(Long jobDetailId, String canonicalName, String response) {
    endJobDetail(JobDetailStatus.COMPLETED, jobDetailId, canonicalName, response);
  }

  @Override
  public void failJobDetail(Long jobDetailId, String canonicalName, String response) {
    endJobDetail(JobDetailStatus.FAILED, jobDetailId, canonicalName, response);
  }

  @Override
  public void retryJobDetail(Long jobDetailId, String canonicalName, String response) {
    updateJobDetail(JobDetailStatus.PROCESSING, jobDetailId, canonicalName, response);
  }

  private Optional<Tuple<Job, List<JobDetail>>> doFind(Long jobId) {
    Optional<Job> optJob = jobRepository.findById(jobId);
    if (!optJob.isPresent()) {
      return Optional.empty();
    }
    Job job = optJob.get();
    List<JobDetail> details = jobDetailRepository.findByJobId(jobId);

    return Optional.of(new Tuple<>(job, details));
  }

  private JobDetail updateJobDetail(JobDetailStatus status, Long id, String type, String response) {
    LocalDateTime now = LocalDateTime.now();
    JobDetail detail = jobDetailRepository.findById(id).get();
    detail.setResponseType(type);
    detail.setResponse(response);
    detail.setLastUpdateTime(now);
    detail.calculateTotalTime();
    detail.setStatus(status);
    jobDetailRepository.save(detail);
    return detail;
  }

  private void endJobDetail(JobDetailStatus status, Long id, String type, String response) {
    JobDetail detail = updateJobDetail(status, id, type, response);
    releaseJob(detail.getJobId());
  }

  private void releaseJob(Long id) {
    LocalDateTime now = LocalDateTime.now();
    CountDownLatch latch = jobLatch.get(id);
    latch.countDown();

    if (latch.getCount() == 0) {
      Job job = jobRepository.findById(id).get();
      job.setStatus(JobStatus.DONE);
      job.setLastUpdateTime(now);
      job.calculateTotalTime();
      jobRepository.save(job);
    }
  }
}
