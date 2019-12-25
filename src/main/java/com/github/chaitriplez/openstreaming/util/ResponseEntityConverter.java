package com.github.chaitriplez.openstreaming.util;

import com.github.chaitriplez.openstreaming.controller.CallFailException;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;

public class ResponseEntityConverter {

  public static <T> ResponseEntity<T> from(Call<T> call) throws CallFailException {
    Response<T> response;
    try {
      response = call.execute();
    } catch (IOException e) {
      throw new CallFailException("Cannot execute remote call", e);
    }

    if (response.isSuccessful()) {
      return ResponseEntity.status(response.code())
          .headers(
              httpHeaders ->
                  response
                      .headers()
                      .toMultimap()
                      .forEach((k, values) -> httpHeaders.addAll(k, values)))
          .body(response.body());
    }

    throw new CallFailException(
        "Remote execute fail with response code: " + response.code(), response);
  }
}
