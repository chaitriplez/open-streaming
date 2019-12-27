package com.github.chaitriplez.openstreaming.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chaitriplez.openstreaming.api.Settrade2LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.Settrade3LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorQueryAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepQueryAPI;
import com.github.chaitriplez.openstreaming.api.SettradeUserAPI;
import com.github.chaitriplez.openstreaming.util.AccessTokenInterceptor;
import com.github.chaitriplez.openstreaming.util.AccessTokenSupplier;
import com.github.chaitriplez.openstreaming.util.HttpLogger;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retrofit.RateLimiterCallAdapter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
@Setter
@EnableConfigurationProperties({OpenStreamingProperties.class, RetrofitProperties.class})
@Configuration
public class APIConfig {

  @Autowired private ObjectMapper mapper;
  @Autowired private OpenStreamingProperties osProp;
  @Autowired private RetrofitProperties retrofitProp;

  @Bean
  public AccessTokenSupplier accessToken() {
    return new AccessTokenSupplier();
  }

  @Bean
  public HttpLoggingInterceptor httpLogger() {
    HttpLoggingInterceptor logger = new HttpLoggingInterceptor(new HttpLogger());
    logger.setLevel(retrofitProp.getHttpLoggingLevel());
    return logger;
  }

  @Bean
  public RateLimiter queryRateLimiter() {
    RateLimiterConfig config =
        RateLimiterConfig.custom()
            .limitRefreshPeriod(retrofitProp.getUpstreamQueryRefreshPeriod())
            .limitForPeriod(retrofitProp.getUpstreamQueryLimit())
            .timeoutDuration(retrofitProp.getUpstreamQueryTimeout())
            .build();

    RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);

    return rateLimiterRegistry.rateLimiter("query");
  }

  @Bean
  public RateLimiter orderRateLimiter() {
    RateLimiterConfig config =
        RateLimiterConfig.custom()
            .limitRefreshPeriod(retrofitProp.getUpstreamOrderRefreshPeriod())
            .limitForPeriod(retrofitProp.getUpstreamOrderLimit())
            .timeoutDuration(retrofitProp.getUpstreamOrderTimeout())
            .build();

    RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);

    return rateLimiterRegistry.rateLimiter("order");
  }

  @Bean
  public Retrofit defaultRetrofit() {

    return new Retrofit.Builder()
        .baseUrl(osProp.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .client(new Builder().addInterceptor(httpLogger()).build())
        .build();
  }

  @Bean
  public Retrofit queryRetrofit() {
    return new Retrofit.Builder()
        .baseUrl(osProp.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RateLimiterCallAdapter.of(queryRateLimiter()))
        .client(
            new Builder()
                .addInterceptor(new AccessTokenInterceptor(accessToken()))
                .addInterceptor(httpLogger())
                .build())
        .build();
  }

  @Bean
  public Retrofit orderRetrofit() {
    return new Retrofit.Builder()
        .baseUrl(osProp.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RateLimiterCallAdapter.of(orderRateLimiter()))
        .client(
            new Builder()
                .addInterceptor(new AccessTokenInterceptor(accessToken()))
                .addInterceptor(httpLogger())
                .build())
        .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "openstreaming", name = "login-type", havingValue = "TWO_LEGGED")
  public Settrade2LeggedLoginAPI settrade2LeggedLoginAPI() {
    return defaultRetrofit().create(Settrade2LeggedLoginAPI.class);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "openstreaming",
      name = "login-type",
      havingValue = "THREE_LEGGED")
  public Settrade3LeggedLoginAPI settrade3LeggedLoginAPI() {
    return defaultRetrofit().create(Settrade3LeggedLoginAPI.class);
  }

  @Bean
  public SettradeUserAPI userAPI() {
    return queryRetrofit().create(SettradeUserAPI.class);
  }

  @ConditionalOnProperty(prefix = "openstreaming", name = "user-type", havingValue = "INVESTOR")
  public final class InvestorAPI {
    @Bean
    public SettradeDerivativesInvestorOrderAPI investorOrderAPI() {
      return orderRetrofit().create(SettradeDerivativesInvestorOrderAPI.class);
    }

    @Bean
    public SettradeDerivativesInvestorQueryAPI investorQueryAPI() {
      return queryRetrofit().create(SettradeDerivativesInvestorQueryAPI.class);
    }
  }

  @ConditionalOnProperty(prefix = "openstreaming", name = "user-type", havingValue = "MKT_REP")
  public final class MktRepAPI {
    @Bean
    public SettradeDerivativesMktRepOrderAPI mktRepOrderAPI() {
      return orderRetrofit().create(SettradeDerivativesMktRepOrderAPI.class);
    }

    @Bean
    public SettradeDerivativesMktRepQueryAPI mktRepQueryAPI() {
      return queryRetrofit().create(SettradeDerivativesMktRepQueryAPI.class);
    }
  }
}
