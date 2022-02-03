package com.github.chaitriplez.openstreaming.controller;

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

  @GetMapping("/api/high/v1/connect")
  public void connect() throws Exception {
    dispatcherService.connect();
  }

  @GetMapping("/api/high/v1/orderpush")
  public void orderPush() throws Exception {
    dispatcherService.syncOrderCache();
  }

  @PostMapping("/api/high/v1/{symbol}")
  public void orderPush(@PathVariable("symbol") String symbol) throws Exception {
    dispatcherService.subscribeSymbol(symbol);
  }
}
