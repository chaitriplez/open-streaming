package com.github.chaitriplez.openstreaming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("openstreaming.three-legged")
public class ThreeLeggedLoginProperties {
  private String loginHost;
  private String apiKey;
  private String apiSecret;
  private String redirectUrl;
  private String scope;
}
