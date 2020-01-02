package com.github.chaitriplez.openstreaming.repository;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("job")
@Data
public class Job {
  @Id private Long id;
  private Integer numberOfJobDetail;
  private String requestType;
  private String request;
  private JobStatus status;
  private LocalDateTime createTime;
  private LocalDateTime lastUpdateTime;
}
