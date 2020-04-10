package com.github.chaitriplez.openstreaming.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openstreaming.push-manager")
public class PushManagerProperties {
  private boolean httpInsecure = false;
  private int keepAliveInterval = 20;
  private boolean autoReconnect = false;
  private int reconnectInterval = 5;
  private int maximumReconnect = 5;
}
