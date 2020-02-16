package com.github.chaitriplez.openstreaming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openstreaming.mqtt")
public class MqttProperties {
  private String streamHost;
  private boolean httpInsecure = false;
  private int keepAliveInterval = 20;
}
