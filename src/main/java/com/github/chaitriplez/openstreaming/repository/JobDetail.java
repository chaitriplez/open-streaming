package com.github.chaitriplez.openstreaming.repository;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("jobDetail")
@Data
public class JobDetail {
  @Id private String id;
  @Indexed private String jobId;
  private String detail;
  private String result;
  private JobStatus status;
}
