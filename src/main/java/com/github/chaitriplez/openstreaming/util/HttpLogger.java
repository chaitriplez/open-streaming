package com.github.chaitriplez.openstreaming.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class HttpLogger implements HttpLoggingInterceptor.Logger {

  @Override
  public void log(String message) {
    log.info(message);
  }
}
