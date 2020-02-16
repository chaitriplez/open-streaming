package com.github.chaitriplez.openstreaming.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Settrade2LeggedLoginAPI {

  @POST("/api/oam/v1/{brokerId}/broker-apps/{appCode}/login")
  Call<AccessTokenResponse> getAccessToken(
      @Path("brokerId") String brokerId,
      @Path("appCode") String appCode,
      @Body AccessToken2LeggedRequest accessTokenRequest);

  @POST("/api/oam/v1/{brokerId}/broker-apps/{appCode}/refresh-token")
  Call<AccessTokenResponse> getRefreshToken(
      @Path("brokerId") String brokerId,
      @Path("appCode") String appCode,
      @Body RefreshToken2LeggedRequest refreshTokenRequest);
}
