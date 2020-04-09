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
public class RefreshToken3LeggedRequest {
  @JsonProperty("authenticated_userid")
  String authenticatedUserid;

  @JsonProperty("broker_id")
  String brokerId;

  @JsonProperty("client_id")
  String clientId;

  @JsonProperty("refresh_token")
  String refreshToken;
}
