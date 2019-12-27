package com.github.chaitriplez.openstreaming.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {
  @JsonProperty("token_type")
  String tokenType;

  @JsonProperty("access_token")
  String accessToken;

  @JsonProperty("refresh_token")
  String refreshToken;

  @JsonProperty("expires_in")
  Long expiresIn;

  @JsonProperty("broker_id")
  String brokerId;

  @JsonProperty("authenticated_userid")
  String authenticatedUserid;
}
