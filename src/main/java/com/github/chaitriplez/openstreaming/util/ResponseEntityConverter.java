package com.github.chaitriplez.openstreaming.util;

import com.github.chaitriplez.openstreaming.controller.CallFailException;
import java.io.IOException;
import java.util.function.Consumer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;

public class ResponseEntityConverter {

  public static <T> ResponseEntity<T> from(Call<T> call) throws CallFailException {
    Response<T> response;
    try {
      response = call.execute();
    } catch (IOException e) {
      throw new CallFailException(
          "Cannot execute remote call",
          e,
          HttpStatus.SERVICE_UNAVAILABLE,
          OpenStreamingConstants.REMOTE_CALL_ERROR_CODE);
    }

    if (response.isSuccessful()) {
      return ResponseEntity.status(response.code())
          .headers(headers(response))
          .body(response.body());
    } else {
      String errorBody;
      try {
        errorBody = response.errorBody().string();
      } catch (IOException e) {
        throw new CallFailException(
            "Cannot read response errorBody",
            e,
            HttpStatus.INTERNAL_SERVER_ERROR,
            OpenStreamingConstants.INTERNAL_ERROR_CODE);
      }
      return (ResponseEntity<T>)
          ResponseEntity.status(response.code()).headers(headers(response)).body(errorBody);
    }
  }

  private static <T> Consumer<HttpHeaders> headers(Response<T> response) {
    return httpHeaders ->
        response.headers().toMultimap().forEach((k, values) -> httpHeaders.addAll(k, values));
  }
}
