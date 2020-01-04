package com.github.chaitriplez.openstreaming.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDetailRepository extends CrudRepository<JobDetail, Long> {
  List<JobDetail> findByJobId(Long jobId);
}
