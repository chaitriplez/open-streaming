package com.github.chaitriplez.openstreaming.util;

import java.util.function.Supplier;
import lombok.Data;

@Data
public class AccessTokenSupplier implements Supplier<String> {

  private String authorization;

  @Override
  public String get() {
    if (authorization == null) {
      throw new IllegalStateException("No access token");
    }
    return authorization;
  }
}
