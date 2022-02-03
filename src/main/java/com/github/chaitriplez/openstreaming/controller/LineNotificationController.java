package com.github.chaitriplez.openstreaming.controller;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  /**
   * Slack compatible endpoint
   */
  @PostMapping("/api-os/line/v1/slack")
  public ResponseEntity sendNotification(@RequestBody JsonNode text) {
    notification.info("{}", text.get("text"));
    Map<String, Object> ok = new HashMap<>();
    ok.put("ok", true);
    return ResponseEntity.ok(ok);
  }
}
