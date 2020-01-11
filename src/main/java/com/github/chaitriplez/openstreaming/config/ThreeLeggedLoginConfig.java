package com.github.chaitriplez.openstreaming.config;

import com.fasterxml.jackson.databind.node.TextNode;
import com.github.chaitriplez.openstreaming.api.AccessToken3LeggedRequest;
import com.github.chaitriplez.openstreaming.api.AccessTokenResponse;
import com.github.chaitriplez.openstreaming.api.ErrorResponse;
import com.github.chaitriplez.openstreaming.api.Settrade3LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.util.AuthorizationSupplier;
import com.github.chaitriplez.openstreaming.util.OpenStreamingConstants;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@Setter
@RestController
@ConditionalOnProperty(prefix = "openstreaming", name = "login-type", havingValue = "THREE_LEGGED")
@EnableConfigurationProperties(ThreeLeggedLoginProperties.class)
@Configuration
public class ThreeLeggedLoginConfig {

  @Autowired private Settrade3LeggedLoginAPI loginAPI;
  @Autowired private OpenStreamingProperties osProp;
  @Autowired private ThreeLeggedLoginProperties loginProp;
  @Autowired private AuthorizationSupplier authorizationSupplier;
  @Autowired private LoginUserInfo userInfo;

  @PostConstruct
  public void init() {
    String loginUrl =
        Settrade3LeggedLoginAPI.loginUrl(
            loginProp.getLoginHost(),
            loginProp.getApiKey(),
            loginProp.getRedirectUrl(),
            loginProp.getScope());
    log.info("Login URL: {}", loginUrl);
  }

  @Bean
  public LoginUserInfo userInfo() {
    return new LoginUserInfo();
  }

  @GetMapping(
      path = "/login",
      params = {"code"})
  public ResponseEntity<?> loginWithAuthCode(@RequestParam("code") String code) throws IOException {
    log.info("Start login with authorization code...");
    if (userInfo.isLogin()) {
      ErrorResponse error =
          ErrorResponse.builder()
              .code(OpenStreamingConstants.LOGIN_ERROR_CODE)
              .message("Already login")
              .build();
      log.info("Login fail: {}", error);
      return ResponseEntity.badRequest().body(error);
    }
    AccessToken3LeggedRequest tokenReq =
        AccessToken3LeggedRequest.builder()
            .client_id(loginProp.getApiKey())
            .code(code)
            .redirect_uri(loginProp.getRedirectUrl())
            .build();
    Call<AccessTokenResponse> call =
        loginAPI.getAccessToken(
            Settrade3LeggedLoginAPI.basicAuthorization(
                loginProp.getApiKey(), loginProp.getApiSecret()),
            tokenReq);

    log.info("Request access token...");
    Response<AccessTokenResponse> response = call.execute();
    if (!response.isSuccessful()) {
      ErrorResponse error =
          ErrorResponse.builder()
              .code(OpenStreamingConstants.LOGIN_ERROR_CODE)
              .message("Login fail")
              .additionalInfo("detail", new TextNode(response.errorBody().string()))
              .build();

      log.info("Login fail: {}", error);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    log.info("Get access token...");
    AccessTokenResponse atRes = response.body();
    if ((!Objects.equals(atRes.getBrokerId(), osProp.getBrokerId()))
        || (!Objects.equals(atRes.getAuthenticatedUserid(), osProp.getUsername()))) {
      ErrorResponse error =
          ErrorResponse.builder()
              .code(OpenStreamingConstants.LOGIN_ERROR_CODE)
              .message("Invalid broker/username")
              .additionalInfo(
                  "detail",
                  new TextNode(
                      "Expected "
                          + osProp.getBrokerId()
                          + "/"
                          + osProp.getUsername()
                          + " but "
                          + atRes.getBrokerId()
                          + "/"
                          + atRes.getAuthenticatedUserid()))
              .build();

      log.info("Login fail: {}", error);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    authorizationSupplier.setAuthorization("Bearer " + atRes.getAccessToken());
    userInfo.setLogin(true);
    userInfo.setBrokerId(atRes.getBrokerId());
    userInfo.setUsername(atRes.getAuthenticatedUserid());
    userInfo.setLoginTime(LocalDateTime.now());

    log.info("Login success: {}", userInfo);
    return ResponseEntity.ok(userInfo);
  }

  @GetMapping("/login")
  public ResponseEntity redirectToLoginPage() {
    log.info("Get login page...");
    String loginUrl =
        Settrade3LeggedLoginAPI.loginUrl(
            loginProp.getLoginHost(),
            loginProp.getApiKey(),
            loginProp.getRedirectUrl(),
            loginProp.getScope());
    return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
        .header(HttpHeaders.LOCATION, loginUrl)
        .build();
  }
}
