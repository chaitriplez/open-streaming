package com.github.chaitriplez.openstreaming.config;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LoginUserInfo {
  private boolean login;
  private String brokerId;
  private String username;
  private LocalDateTime loginTime;
}
