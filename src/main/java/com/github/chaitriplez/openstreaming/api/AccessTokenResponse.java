package com.github.chaitriplez.openstreaming.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {
  String token_type;
  String access_token;
  String refresh_token;
  Long expires_in;
  String broker_id;
  String authenticated_userid;
}
