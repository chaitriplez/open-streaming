package com.github.chaitriplez.openstreaming.repository;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends PagingAndSortingRepository<Job, Long> {
  Optional<Job> findTopByOrderByIdDesc();
}
