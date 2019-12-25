package com.github.chaitriplez.openstreaming.service;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OSUserInfo {
  private boolean login;
  private String brokerId;
  private String username;
  private LocalDateTime loginTime;
}
