package com.github.chaitriplez.openstreaming.component;

/** Interval query order from remote server and update to order cache */
public interface PullOrderWorker {

  void monitor(Long orderNo);

  void stopMonitor(Long orderNo);
}
