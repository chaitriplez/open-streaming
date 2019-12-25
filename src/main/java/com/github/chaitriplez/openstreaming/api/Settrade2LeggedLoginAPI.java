package com.github.chaitriplez.openstreaming.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Settrade2LeggedLoginAPI {

  @POST("/api/oam/v1/{brokerId}/broker-apps/{appId}/login")
  Call<AccessTokenResponse> getAccessToken(
      @Path("brokerId") String brokerId,
      @Path("appId") String appId,
      @Body AccessToken2LeggedRequest accessTokenRequest);
}
