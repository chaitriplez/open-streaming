package com.github.chaitriplez.openstreaming.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SettradeUserAPI {

  @GET("/api/um/v1/{brokerId}/user/me")
  Call<ObjectNode> getUserInfo(@Path("brokerId") String brokerId);
}
