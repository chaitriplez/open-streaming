package com.github.chaitriplez.openstreaming.repository;

import java.util.Optional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Setter
@Component
public class JobRepositorySync implements JobRepository {

  @Autowired private JobRepository jobRepository;

  @Override
  public synchronized <S extends Job> S save(S entity) {
    return jobRepository.save(entity);
  }

  @Override
  public synchronized <S extends Job> Iterable<S> saveAll(Iterable<S> entities) {
    return jobRepository.saveAll(entities);
  }

  @Override
  public synchronized Optional<Job> findById(Long aLong) {
    return jobRepository.findById(aLong);
  }

  @Override
  public synchronized boolean existsById(Long aLong) {
    return jobRepository.existsById(aLong);
  }

  @Override
  public synchronized Iterable<Job> findAll() {
    return jobRepository.findAll();
  }

  @Override
  public synchronized Iterable<Job> findAllById(Iterable<Long> longs) {
    return jobRepository.findAllById(longs);
  }

  @Override
  public synchronized long count() {
    return jobRepository.count();
  }

  @Override
  public synchronized void deleteById(Long aLong) {
    jobRepository.deleteById(aLong);
  }

  @Override
  public synchronized void delete(Job entity) {
    jobRepository.delete(entity);
  }

  @Override
  public synchronized void deleteAll(Iterable<? extends Job> entities) {
    jobRepository.deleteAll(entities);
  }

  @Override
  public synchronized void deleteAll() {
    jobRepository.deleteAll();
  }
}
