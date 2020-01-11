package com.github.chaitriplez.openstreaming.component;

import java.util.concurrent.CountDownLatch;

public interface JobLatch {
  CountDownLatch get(Long jobId);

  CountDownLatch create(Long jobId, int count);

  void clear();
}
