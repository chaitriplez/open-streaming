package com.github.chaitriplez.openstreaming.repository;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("jobDetail")
@Data
public class JobDetail {
  @Id private Long id;
  @Indexed private Long jobId;
  private String requestType;
  private String request;
  private String responseType;
  private String response;
  private JobStatus status;
  private LocalDateTime executionTime;
  private LocalDateTime lastUpdateTime;
}
