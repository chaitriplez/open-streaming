package com.github.chaitriplez.openstreaming.controller;

import java.util.Optional;
import retrofit2.Response;

public class CallFailException extends RuntimeException {
  private Response response;

  public CallFailException(String message, Response response) {
    super(message);
    this.response = response;
  }

  public CallFailException(String message, Throwable cause) {
    super(message, cause);
  }

  public Optional<Response> getResponse() {
    return Optional.ofNullable(response);
  }
}
