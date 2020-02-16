package com.github.chaitriplez.openstreaming.component;

public interface PushListener<T> {
  T from(byte[] bytes);

  void onMessage(T t);

  default void receive(byte[] bytes) {
    onMessage(from(bytes));
  }
}
