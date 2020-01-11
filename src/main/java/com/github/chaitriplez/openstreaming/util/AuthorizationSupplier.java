package com.github.chaitriplez.openstreaming.util;

import com.github.chaitriplez.openstreaming.controller.CallFailException;
import java.util.function.Supplier;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class AuthorizationSupplier implements Supplier<String> {

  private String authorization;

  @Override
  public String get() {
    if (authorization == null) {
      throw new CallFailException(
          "No access token", HttpStatus.UNAUTHORIZED, OpenStreamingConstants.LOGIN_ERROR_CODE);
    }
    return authorization;
  }
}
