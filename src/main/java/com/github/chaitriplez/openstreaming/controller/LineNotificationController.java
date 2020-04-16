package com.github.chaitriplez.openstreaming.controller;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Setter
@RestController
public class LineNotificationController {

  private static final Logger notification = LoggerFactory.getLogger("notification.line");

  @PostMapping("/api-os/line/v1/push")
  public void sendNotification(@RequestParam(name = "message") String message) {
    notification.info("{}", message);
  }
}
