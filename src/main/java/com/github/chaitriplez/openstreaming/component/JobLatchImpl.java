package com.github.chaitriplez.openstreaming.component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Component;

@Component
public class JobLatchImpl implements JobLatch {

  private static final CountDownLatch ZERO = new CountDownLatch(0);
  private final ConcurrentMap<String, CountDownLatch> latches = new ConcurrentHashMap<>();

  @Override
  public CountDownLatch get(String jobId) {
    return latches.getOrDefault(jobId, ZERO);
  }

  @Override
  public CountDownLatch create(String jobId, int count) {
    if (latches.containsKey(jobId)) {
      throw new IllegalStateException("Duplicate latch: " + jobId);
    }
    return latches.putIfAbsent(jobId, new CountDownLatch(count));
  }
}
