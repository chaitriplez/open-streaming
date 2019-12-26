package com.github.chaitriplez.openstreaming.config;

import com.github.chaitriplez.openstreaming.util.IpFilter;
import javax.servlet.Filter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RemoteAddrFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Slf4j
@Data
@Configuration
@ConfigurationProperties("openstreaming.ip-filter")
@ConditionalOnProperty(prefix = "openstreaming.ip-filter", name = "enabled", havingValue = "true")
public class IpFilterConfig {

  private String allow;
  private String deny;
  private int denyStatus;

  @Bean
  public Filter ipFilter() {
    log.info("Enable ip filter: allow[{}] deny[{}] denyStatus[{}]", allow, deny, HttpStatus.valueOf(denyStatus));
    IpFilter filter = new IpFilter();
    filter.setAllow(allow);
    filter.setDeny(deny);
    filter.setDenyStatus(denyStatus);
    return filter;
  }
}
