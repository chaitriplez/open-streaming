package com.github.chaitriplez.openstreaming.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Settrade3LeggedLoginAPI {

  static String loginUrl(String host, String clientId, String redirectUrl, String scope) {
    try {
      URL baseUrl = new URL(host);
      URL relativeUrl =
          new URL(
              baseUrl,
              "/login-portal/login?response_type=code&client_id="
                  + URLEncoder.encode(clientId, "UTF-8")
                  + "&redirect_uri="
                  + URLEncoder.encode(redirectUrl, "UTF-8")
                  + "&scope="
                  + URLEncoder.encode(scope, "UTF-8"));

      return relativeUrl.toString();
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Invalid host:" + host, e);
    }
  }

  static String basicAuthorization(String apiKey, String apiSecret) {
    String header = apiKey + ":" + apiSecret;
    String encoded = Base64.getEncoder().encodeToString(header.getBytes());
    return "Basic " + encoded;
  }

  /**
   * @param authorization "Basic base64_encode(client_id:client_secret)"
   * @param accessTokenRequest
   * @return
   */
  @POST("/api/oam/v1/oauth2/token?grant_type=authorization_code")
  Call<AccessTokenResponse> getAccessToken(
      @Header("Authorization") String authorization,
      @Body AccessToken3LeggedRequest accessTokenRequest);

  /**
   * @param authorization "Basic base64_encode(client_id:client_secret)"
   * @param refreshTokenRequest
   * @return
   */
  @POST("/api/oam/v1/oauth2/token?grant_type=refresh_token")
  Call<AccessTokenResponse> getAccessTokenFromRefreshToken(
      @Header("Authorization") String authorization,
      @Body RefreshToken3LeggedRequest refreshTokenRequest);
}
