package com.github.chaitriplez.openstreaming.api.line;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MessageAPI {
  @POST("/v2/bot/message/push")
  Call<JsonNode> pushMessage(@Header("Authorization") String token, @Body PushRequest request);
}
