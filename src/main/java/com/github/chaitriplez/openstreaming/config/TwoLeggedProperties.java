package com.github.chaitriplez.openstreaming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("openstreaming.two-legged")
public class TwoLeggedProperties {
  private String appId;
  private String apiKey;
  private String apiSecret;
}

