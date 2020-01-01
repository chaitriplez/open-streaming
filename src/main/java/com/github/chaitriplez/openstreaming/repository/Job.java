package com.github.chaitriplez.openstreaming.repository;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("job")
@Data
public class Job {
  @Id private String id;
  private Integer numberOfJobDetail;
  private JobStatus status;
}
