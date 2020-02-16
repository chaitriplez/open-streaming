package com.github.chaitriplez.openstreaming.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.chaitriplez.openstreaming.api.AccessToken2LeggedRequest;
import com.github.chaitriplez.openstreaming.api.AccessTokenResponse;
import com.github.chaitriplez.openstreaming.api.Settrade2LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.SettradeUserAPI;
import com.github.chaitriplez.openstreaming.util.AuthorizationHeaderInterceptor;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
public class OAuth2LeggedLogin {
  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      log.info("Usage: java {} {}", OAuth2LeggedLogin.class.getName(), "config.file");
      System.exit(-1);
    }
    final Properties properties = new Properties();
    try (InputStream is = OAuth2LeggedLogin.class.getClassLoader().getResourceAsStream(args[0])) {
      properties.load(is);
    }

    final String API_HOST = properties.getProperty("API_HOST");
    final String BROKER_ID = properties.getProperty("BROKER_ID");
    final String APP_CODE = properties.getProperty("APP_CODE");
    final String API_KEY = properties.getProperty("API_KEY");
    final String API_SECRET = properties.getProperty("API_SECRET");

    Supplier<String> authorization;
    {
      Settrade2LeggedLoginAPI loginAPI =
          new Retrofit.Builder()
              .baseUrl(API_HOST)
              .addConverterFactory(JacksonConverterFactory.create())
              .build()
              .create(Settrade2LeggedLoginAPI.class);

      AccessToken2LeggedRequest tokenReq =
          AccessToken2LeggedRequest.builder().params("").apiKey(API_KEY).build();
      tokenReq.sign(API_SECRET);
      Call<AccessTokenResponse> call = loginAPI.getAccessToken(BROKER_ID, APP_CODE, tokenReq);
      log.info("Request Info: {}", call.request());

      Response<AccessTokenResponse> result = call.execute();
      if (result.isSuccessful()) {
        AccessTokenResponse accessToken = result.body();
        authorization = () -> "Bearer " + accessToken.getAccessToken();
        log.info("Access Token: {}", accessToken);
      } else {
        log.error("Cannot get access token: {}", result.errorBody().string());
        return;
      }
    }

    {
      SettradeUserAPI userAPI =
          new Retrofit.Builder()
              .baseUrl(API_HOST)
              .addConverterFactory(JacksonConverterFactory.create())
              .client(
                  new OkHttpClient()
                      .newBuilder()
                      .addInterceptor(new AuthorizationHeaderInterceptor(authorization))
                      .build())
              .build()
              .create(SettradeUserAPI.class);

      {
        Call<JsonNode> call = userAPI.getUserInfo(BROKER_ID);
        log.info("Request Info: {}", call.request());

        Response<JsonNode> result = call.execute();
        if (result.isSuccessful()) {
          log.info("User Info: {}", result.body());
        } else {
          log.error("Cannot get user info: {}", result.errorBody().toString());
          return;
        }
      }
    }
  }
}
