package com.github.chaitriplez.openstreaming.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
  private JobDetailStatus status;
  private LocalDateTime executionTime;
  private LocalDateTime lastUpdateTime;
  private Long totalTime;

  public void calculateTotalTime() {
    totalTime =
        executionTime != null && lastUpdateTime != null
            ? lastUpdateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
                - executionTime.toInstant(ZoneOffset.UTC).toEpochMilli()
            : -1;
  }
}
