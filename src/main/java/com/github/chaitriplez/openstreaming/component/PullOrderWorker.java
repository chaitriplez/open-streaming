package com.github.chaitriplez.openstreaming.component;

public interface PullOrderWorker {

  void monitor(Long orderNo);

  void stopMonitor(Long orderNo);
}
