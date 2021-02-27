package com.github.chaitriplez.openstreaming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openstreaming")
public class OpenStreamingProperties {
  private String brokerId;
  private String username;
  private String accountNo;
  private String pin;
  private String apiHost;
}
