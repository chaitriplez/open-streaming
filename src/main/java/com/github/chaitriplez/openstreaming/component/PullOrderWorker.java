package com.github.chaitriplez.openstreaming.component;

public interface PullOrderWorker {

  /**
   * Interval query order from remote server and update to order cache until balance qty is ZERO
   *
   * @param orderNo
   */
  void monitor(Long orderNo);

  void stopMonitor(Long orderNo);
}
