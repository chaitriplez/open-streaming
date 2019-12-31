package com.github.chaitriplez.openstreaming.component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@Data
@ConfigurationProperties(prefix = "openstreaming.pull-order")
public class PullOrderProperties {

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration checkInterval = Duration.ofSeconds(5);
}
