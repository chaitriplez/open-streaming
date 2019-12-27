package com.github.chaitriplez.openstreaming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openstreaming.two-legged")
public class TwoLeggedLoginProperties {
  private String appId;
  private String apiKey;
  private String apiSecret;
}
