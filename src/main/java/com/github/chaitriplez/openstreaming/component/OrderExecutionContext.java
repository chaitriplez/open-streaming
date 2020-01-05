package com.github.chaitriplez.openstreaming.component;

@FunctionalInterface
public interface OrderExecutionContext {
  Object getObject(Class<?> clazz);

  default <T> T getBean(Class<T> clazz) {
    return (T) getObject(clazz);
  }
}
