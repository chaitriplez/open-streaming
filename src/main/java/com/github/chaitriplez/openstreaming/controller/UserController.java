package com.github.chaitriplez.openstreaming.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.chaitriplez.openstreaming.api.SettradeUserAPI;
import com.github.chaitriplez.openstreaming.util.ResponseEntityConverter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Setter
@RestController
public class UserController {

  @Autowired private SettradeUserAPI api;

  @GetMapping("/api/um/v1/{brokerId}/user/me")
  ResponseEntity<ObjectNode> getUserInfo(@PathVariable("brokerId") String brokerId) {
    return ResponseEntityConverter.from(api.getUserInfo(brokerId));
  }
}
