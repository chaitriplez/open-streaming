package com.github.chaitriplez.openstreaming.repository;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDetailRepository extends PagingAndSortingRepository<JobDetail, String> {
  List<JobDetail> finByJobId(String jobId);
}
