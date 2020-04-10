package com.github.chaitriplez.openstreaming.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SettradeStreamAPI {

  @GET("/api/dispatcher/v1/{brokerId}/token")
  Call<PreConnectStreamResponse> preConnect(@Path("brokerId") String brokerId);
}
