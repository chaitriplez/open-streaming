package com.github.chaitriplez.openstreaming.component;

public interface PushManager {
  void start() throws Exception;

  void stop() throws Exception;

  boolean isConnected();

  void subscribe(String topic, PushListener listener) throws Exception;

  void unsubscribe(String topic, PushListener listener) throws Exception;

  void unsubscribe(String topic) throws Exception;
}
