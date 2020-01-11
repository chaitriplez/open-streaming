package com.github.chaitriplez.openstreaming.util;

import java.io.IOException;
import java.util.function.Supplier;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationHeaderInterceptor implements Interceptor {

  private final Supplier<String> authorization;

  public AuthorizationHeaderInterceptor(Supplier<String> authorization) {
    this.authorization = authorization;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request original = chain.request();

    Request request = original.newBuilder().header("Authorization", authorization.get()).build();

    return chain.proceed(request);
  }
}
