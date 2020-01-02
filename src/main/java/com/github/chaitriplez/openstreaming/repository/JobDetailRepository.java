package com.github.chaitriplez.openstreaming.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDetailRepository extends PagingAndSortingRepository<JobDetail, Long> {
  Optional<JobDetail> findTopByOrderByIdDesc();

  List<JobDetail> findByJobId(Long jobId);
}
