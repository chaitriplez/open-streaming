package com.github.chaitriplez.openstreaming.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@Data
@ConfigurationProperties(prefix = "openstreaming.retrofit")
public class RetrofitProperties {

  private boolean httpInsecure = false;

  private HttpLoggingInterceptor.Level httpLoggingLevel = Level.NONE;

  private int upstreamQueryLimit = 5;

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration upstreamQueryRefreshPeriod = Duration.ofSeconds(1);

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration upstreamQueryTimeout = Duration.ofSeconds(15);

  private int upstreamOrderLimit = 5;

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration upstreamOrderRefreshPeriod = Duration.ofSeconds(1);

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration upstreamOrderTimeout = Duration.ofSeconds(15);

  private int upstreamPostLoginLimit = 5;

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration upstreamPostLoginRefreshPeriod = Duration.ofSeconds(5);

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration upstreamPostLoginTimeout = Duration.ofSeconds(15);
}
