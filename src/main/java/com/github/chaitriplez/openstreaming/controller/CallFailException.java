package com.github.chaitriplez.openstreaming.controller;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CallFailException extends RuntimeException {

  private HttpStatus httpStatus;
  private String errorCode;

  public CallFailException(String message, HttpStatus httpStatus, String errorCode) {
    super(message);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
  }

  public CallFailException(
      String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
    super(message, cause);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
  }
}
