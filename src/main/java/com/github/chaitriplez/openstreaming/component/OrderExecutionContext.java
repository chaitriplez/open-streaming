package com.github.chaitriplez.openstreaming.component;

public interface OrderExecutionContext {
  <T> T getBean(Class<T> requiredType);
}
