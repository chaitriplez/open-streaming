package com.github.chaitriplez.openstreaming.api;

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
  String client_id;
  String code;
  String redirect_uri;
}
