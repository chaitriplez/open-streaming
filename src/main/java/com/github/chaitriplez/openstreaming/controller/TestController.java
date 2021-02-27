package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.api.AccessTokenResponse;
import com.github.chaitriplez.openstreaming.api.RefreshToken3LeggedRequest;
import com.github.chaitriplez.openstreaming.api.Settrade3LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.config.ThreeLeggedLoginProperties;
import com.github.chaitriplez.openstreaming.service.PushInfoServiceImpl;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Setter
@RestController
public class TestController {

  @Autowired private PushInfoServiceImpl dispatcherService;

  @Autowired(required = false)
  private Settrade3LeggedLoginAPI loginAPI;

  @Autowired(required = false)
  private ThreeLeggedLoginProperties loginProp;

  @GetMapping("/api/high/v1/orderpush")
  public void orderPush() throws Exception {
    dispatcherService.connect();
    dispatcherService.syncOrderCache();
  }

  @PostMapping("/api/high/v1/{symbol}")
  public void orderPush(@PathVariable("symbol") String symbol) throws Exception {
    dispatcherService.subscribeSymbol(symbol);
  }

  @GetMapping("/api/high/v1/refresh")
  public AccessTokenResponse orderPush(RefreshToken3LeggedRequest accessTokenRequest)
      throws Exception {
    return loginAPI
        .getAccessTokenFromRefreshToken(
            Settrade3LeggedLoginAPI.basicAuthorization(
                loginProp.getApiKey(), loginProp.getApiSecret()),
            accessTokenRequest)
        .execute()
        .body();
  }
}
