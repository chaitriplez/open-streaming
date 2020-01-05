package com.github.chaitriplez.openstreaming.repository;

import java.util.List;
import java.util.Optional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Setter
@Component
public class JobDetailRepositorySync implements JobDetailRepository {

  @Autowired private JobDetailRepository jobDetailRepository;

  @Override
  public synchronized List<JobDetail> findByJobId(Long jobId) {
    return jobDetailRepository.findByJobId(jobId);
  }

  @Override
  public synchronized <S extends JobDetail> S save(S entity) {
    return jobDetailRepository.save(entity);
  }

  @Override
  public synchronized <S extends JobDetail> Iterable<S> saveAll(Iterable<S> entities) {
    return jobDetailRepository.saveAll(entities);
  }

  @Override
  public synchronized Optional<JobDetail> findById(Long aLong) {
    return jobDetailRepository.findById(aLong);
  }

  @Override
  public synchronized boolean existsById(Long aLong) {
    return jobDetailRepository.existsById(aLong);
  }

  @Override
  public synchronized Iterable<JobDetail> findAll() {
    return jobDetailRepository.findAll();
  }

  @Override
  public synchronized Iterable<JobDetail> findAllById(Iterable<Long> longs) {
    return jobDetailRepository.findAllById(longs);
  }

  @Override
  public synchronized long count() {
    return jobDetailRepository.count();
  }

  @Override
  public synchronized void deleteById(Long aLong) {
    jobDetailRepository.deleteById(aLong);
  }

  @Override
  public synchronized void delete(JobDetail entity) {
    jobDetailRepository.delete(entity);
  }

  @Override
  public synchronized void deleteAll(Iterable<? extends JobDetail> entities) {
    jobDetailRepository.deleteAll(entities);
  }

  @Override
  public synchronized void deleteAll() {
    jobDetailRepository.deleteAll();
  }
}
