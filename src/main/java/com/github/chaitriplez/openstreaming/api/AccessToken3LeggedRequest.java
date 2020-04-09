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
public class AccessToken3LeggedRequest {
  @Builder.Default final String grant_type = "authorization_code";

  @JsonProperty("client_id")
  String clientId;

  String code;

  @JsonProperty("redirect_uri")
  String redirectUri;
}
