package com.github.chaitriplez.openstreaming.example;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.chaitriplez.openstreaming.api.AccessToken3LeggedRequest;
import com.github.chaitriplez.openstreaming.api.AccessTokenResponse;
import com.github.chaitriplez.openstreaming.api.Settrade3LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.SettradeUserAPI;
import com.github.chaitriplez.openstreaming.util.AccessTokenInterceptor;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
public class OAuth3LeggedLogin {
  public static void main(String[] args) throws Exception {

    if(args.length != 1) {
      log.info("Usage: java {} {}", OAuth3LeggedLogin.class.getName(), "config.file");
      System.exit(-1);
    }
    final Properties properties = new Properties();
    try (InputStream is = OAuth3LeggedLogin.class.getClassLoader().getResourceAsStream(args[0])) {
      properties.load(is);
    }

    final String LOGIN_HOST = properties.getProperty("LOGIN_HOST");
    final String API_HOST = properties.getProperty("API_HOST");
    final String BROKER_ID = properties.getProperty("BROKER_ID");
    final String API_KEY = properties.getProperty("API_KEY");
    final String API_SECRET = properties.getProperty("API_SECRET");
    final String REDIRECT_URL = properties.getProperty("REDIRECT_URL");
    final String SCOPE = properties.getProperty("SCOPE");

    String loginUrl = Settrade3LeggedLoginAPI.loginUrl(LOGIN_HOST, API_KEY, REDIRECT_URL, SCOPE);
    log.info("Login and get authorization code from redirectUrl: {}", loginUrl);

    System.out.print("Enter authorization code: ");
    Scanner sc = new Scanner(System.in);
    String authCode = sc.next();
    sc.close();

    Supplier<String> authorization;
    {
      Settrade3LeggedLoginAPI loginAPI =
          new Retrofit.Builder()
              .baseUrl(API_HOST)
              .addConverterFactory(JacksonConverterFactory.create())
              .build()
              .create(Settrade3LeggedLoginAPI.class);

      AccessToken3LeggedRequest tokenReq =
          AccessToken3LeggedRequest.builder()
              .client_id(API_KEY)
              .code(authCode)
              .redirect_uri(REDIRECT_URL)
              .build();
      Call<AccessTokenResponse> call =
          loginAPI.getAccessToken(
              Settrade3LeggedLoginAPI.basicAuthorization(API_KEY, API_SECRET), tokenReq);
      log.info("Request Info: {}", call.request());

      Response<AccessTokenResponse> result = call.execute();
      if (result.isSuccessful()) {
        AccessTokenResponse accessToken = result.body();
        authorization = () -> "Bearer " + accessToken.getAccess_token();
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
                      .addInterceptor(new AccessTokenInterceptor(authorization))
                      .build())
              .build()
              .create(SettradeUserAPI.class);
      Call<ObjectNode> call = userAPI.getUserInfo(BROKER_ID);
      log.info("Request Info: {}", call.request());

      Response<ObjectNode> result = call.execute();
      if (result.isSuccessful()) {
        log.info("User Info: {}", result.body());
      } else {
        log.error("Cannot get user info: {}", result.errorBody().toString());
        return;
      }
    }
  }
}
