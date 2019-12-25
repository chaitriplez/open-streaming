package com.github.chaitriplez.openstreaming.config;

import com.github.chaitriplez.openstreaming.api.AccessToken2LeggedRequest;
import com.github.chaitriplez.openstreaming.api.AccessTokenResponse;
import com.github.chaitriplez.openstreaming.api.Settrade2LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.service.OSUserInfo;
import com.github.chaitriplez.openstreaming.util.AccessTokenSupplier;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@Setter
@ConditionalOnProperty(prefix = "openstreaming", name = "login-type", havingValue = "TWO_LEGGED")
@EnableConfigurationProperties(TwoLeggedLoginProperties.class)
@Configuration
public class TwoLeggedLoginConfig {

  @Autowired private Settrade2LeggedLoginAPI loginAPI;
  @Autowired private OpenStreamingProperties osProp;
  @Autowired private TwoLeggedLoginProperties loginProp;
  @Autowired private AccessTokenSupplier accessTokenSupplier;

  @Bean
  public OSUserInfo processLogin() throws IOException {
    log.info("Start login...");
    AccessToken2LeggedRequest tokenReq =
        AccessToken2LeggedRequest.builder().apiKey(loginProp.getApiKey()).params("").build();
    tokenReq.sign(loginProp.getApiSecret());
    Call<AccessTokenResponse> call =
        loginAPI.getAccessToken(osProp.getBrokerId(), loginProp.getAppId(), tokenReq);

    log.info("Request access token...");
    Response<AccessTokenResponse> response = call.execute();
    if (!response.isSuccessful()) {
      throw new IllegalStateException("Login fail: " + response.errorBody().string());
    }

    log.info("Get access token...");
    AccessTokenResponse atRes = response.body();
    accessTokenSupplier.setAuthorization("Bearer " + atRes.getAccess_token());

    OSUserInfo userInfo = new OSUserInfo();
    userInfo.setLogin(true);
    userInfo.setBrokerId(atRes.getBroker_id());
    userInfo.setUsername(atRes.getAuthenticated_userid());
    userInfo.setLoginTime(LocalDateTime.now());

    log.info("Login success: {}", userInfo);
    return userInfo;
  }
}
