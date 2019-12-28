package com.github.chaitriplez.openstreaming.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.github.chaitriplez.openstreaming.api.ErrorResponse;
import com.github.chaitriplez.openstreaming.util.OpenStreamingConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(CallFailException.class)
  protected ResponseEntity<Object> handleCallFailException(CallFailException ex) {
    ErrorResponse.ErrorResponseBuilder builder =
        ErrorResponse.builder().code(ex.getErrorCode()).message(ex.getMessage());
    if (ex.getCause() != null) {
      builder = builder.additionalInfo("cause", new TextNode(ex.getCause().getMessage()));
    }
    return ResponseEntity.status(ex.getHttpStatus()).body(builder.build());
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    body =
        body == null
            ? ErrorResponse.builder()
                .code(OpenStreamingConstants.INTERNAL_ERROR_CODE)
                .message(ex.getMessage())
                .build()
            : null;
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }
}
