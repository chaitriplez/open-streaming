package com.github.chaitriplez.openstreaming.controller;

import java.util.Set;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Setter
@RestController
public class RedisController {

  @Autowired private StringRedisTemplate redis;

  @GetMapping("/api-os/redis/v1/hget/{key}/{field}")
  public ResponseEntity hget(@PathVariable("key") String key, @PathVariable("field") String field) {
    Object data = redis.opsForHash().get(key, field);
    if(data == null) {
      return ResponseEntity.badRequest().body("N/A");
    }
    return ResponseEntity.ok(data);
  }

  @GetMapping("/api-os/redis/v1/smembers/{key}")
  public ResponseEntity smembers(@PathVariable("key") String key) {
    Set<String> data = redis.opsForSet().members(key);
    if(data == null) {
      return ResponseEntity.badRequest().body("N/A");
    }
    return ResponseEntity.ok(String.join("\n", data));
  }
}
