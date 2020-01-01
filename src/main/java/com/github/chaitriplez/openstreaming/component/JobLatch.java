package com.github.chaitriplez.openstreaming.component;

import java.util.concurrent.CountDownLatch;

public interface JobLatch {
  CountDownLatch get(String jobId);

  CountDownLatch create(String jobId, int count);
}
