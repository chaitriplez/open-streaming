package com.github.chaitriplez.openstreaming.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SettradeUserAPI {

  @GET("/api/um/v1/{brokerId}/user/me")
  Call<JsonNode> getUserInfo(@Path("brokerId") String brokerId);
}
