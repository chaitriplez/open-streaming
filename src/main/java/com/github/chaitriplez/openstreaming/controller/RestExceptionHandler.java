package com.github.chaitriplez.openstreaming.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.github.chaitriplez.openstreaming.api.ErrorResponse;
import com.github.chaitriplez.openstreaming.util.OpenStreamingConstants;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import retrofit2.Response;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(CallFailException.class)
  protected ResponseEntity<Object> handleCallFailException(CallFailException ex)
      throws IOException {
    if (ex.getResponse().isPresent()) {
      Response response = ex.getResponse().get();
      return ResponseEntity.status(response.code())
          .headers(
              httpHeaders ->
                  response
                      .headers()
                      .toMultimap()
                      .forEach((k, values) -> httpHeaders.addAll(k, values)))
          .body(response.errorBody().string());
    } else {
      log.error("Cannot call remote server", ex);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ErrorResponse.builder()
                  .code(OpenStreamingConstants.REMOTE_CALL_ERROR_CODE)
                  .message(ex.getMessage())
                  .additionalInfo("cause", new TextNode(ex.getCause().getMessage()))
                  .build());
    }
  }
}
