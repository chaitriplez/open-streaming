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
  private UserType userType;
  private LoginType loginType;
  private String apiHost;

  public enum UserType {
    INVESTOR,
    MKT_REP
  }

  public enum LoginType {
    TWO_LEGGED,
    THREE_LEGGED
  }
}
