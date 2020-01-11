package com.github.chaitriplez.openstreaming.component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobLatchImpl implements JobLatch {

  private static final CountDownLatch ZERO = new CountDownLatch(0);
  private final ConcurrentMap<Long, CountDownLatch> latches = new ConcurrentHashMap<>();

  @Override
  public CountDownLatch get(Long jobId) {
    return latches.getOrDefault(jobId, ZERO);
  }

  @Override
  public CountDownLatch create(Long jobId, int count) {
    if (latches.containsKey(jobId)) {
      throw new IllegalStateException("Duplicate latch: " + jobId);
    }
    return latches.putIfAbsent(jobId, new CountDownLatch(count));
  }

  @Override
  public void clear() {
    latches.clear();
  }
}
