package com.github.chaitriplez.openstreaming.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chaitriplez.openstreaming.api.Settrade2LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.Settrade3LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorQueryAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepQueryAPI;
import com.github.chaitriplez.openstreaming.api.SettradeStreamAPI;
import com.github.chaitriplez.openstreaming.api.SettradeUserAPI;
import com.github.chaitriplez.openstreaming.util.AuthorizationHeaderInterceptor;
import com.github.chaitriplez.openstreaming.util.AuthorizationSupplier;
import com.github.chaitriplez.openstreaming.util.HttpLogger;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retrofit.RateLimiterCallAdapter;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
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

  private static OkHttpClient.Builder ignoreCert(OkHttpClient.Builder builder) {
    log.warn("Enable insecure connection. (Development Environment)");
    try {
      final X509TrustManager trustManager =
          new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[] {};
            }
          };
      final TrustManager[] trustAllCerts = new TrustManager[] {trustManager};

      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new SecureRandom());
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
      builder.hostnameVerifier((hostname, session) -> true);
    } catch (Exception e) {
      log.warn("Cannot config insecure connection.", e);
    }
    return builder;
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public OkHttpClient.Builder httpClientBuilder() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    if (retrofitProp.getHttpLoggingLevel() != Level.NONE) {
      HttpLoggingInterceptor logger = new HttpLoggingInterceptor(new HttpLogger());
      logger.setLevel(retrofitProp.getHttpLoggingLevel());
      builder.addInterceptor(logger);
    }

    if (retrofitProp.isHttpInsecure()) {
      ignoreCert(builder);
    }

    return builder;
  }

  @Bean
  public AuthorizationSupplier authorization() {
    return new AuthorizationSupplier();
  }

  @Bean
  public RateLimiter postLoginRateLimiter() {
    RateLimiterConfig config =
        RateLimiterConfig.custom()
            .limitRefreshPeriod(retrofitProp.getUpstreamPostLoginRefreshPeriod())
            .limitForPeriod(retrofitProp.getUpstreamPostLoginLimit())
            .timeoutDuration(retrofitProp.getUpstreamPostLoginTimeout())
            .build();

    return RateLimiterRegistry.of(config).rateLimiter("postLogin");
  }

  @Bean
  public RateLimiter queryRateLimiter() {
    RateLimiterConfig config =
        RateLimiterConfig.custom()
            .limitRefreshPeriod(retrofitProp.getUpstreamQueryRefreshPeriod())
            .limitForPeriod(retrofitProp.getUpstreamQueryLimit())
            .timeoutDuration(retrofitProp.getUpstreamQueryTimeout())
            .build();

    return RateLimiterRegistry.of(config).rateLimiter("query");
  }

  @Bean
  public RateLimiter orderRateLimiter() {
    RateLimiterConfig config =
        RateLimiterConfig.custom()
            .limitRefreshPeriod(retrofitProp.getUpstreamOrderRefreshPeriod())
            .limitForPeriod(retrofitProp.getUpstreamOrderLimit())
            .timeoutDuration(retrofitProp.getUpstreamOrderTimeout())
            .build();

    return RateLimiterRegistry.of(config).rateLimiter("order");
  }

  @Bean
  public Retrofit preLoginRetrofit() {
    return new Retrofit.Builder()
        .baseUrl(osProp.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .client(httpClientBuilder().build())
        .build();
  }

  @Bean
  public Retrofit postLoginRetrofit() {
    return new Retrofit.Builder()
        .baseUrl(osProp.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RateLimiterCallAdapter.of(postLoginRateLimiter()))
        .client(
            httpClientBuilder()
                .addInterceptor(new AuthorizationHeaderInterceptor(authorization()))
                .build())
        .build();
  }

  @Bean
  public Retrofit queryRetrofit() {
    return new Retrofit.Builder()
        .baseUrl(osProp.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RateLimiterCallAdapter.of(queryRateLimiter()))
        .client(
            httpClientBuilder()
                .addInterceptor(new AuthorizationHeaderInterceptor(authorization()))
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
            httpClientBuilder()
                .addInterceptor(new AuthorizationHeaderInterceptor(authorization()))
                .build())
        .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "openstreaming", name = "login-type", havingValue = "TWO_LEGGED")
  public Settrade2LeggedLoginAPI settrade2LeggedLoginAPI() {
    return preLoginRetrofit().create(Settrade2LeggedLoginAPI.class);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "openstreaming",
      name = "login-type",
      havingValue = "THREE_LEGGED")
  public Settrade3LeggedLoginAPI settrade3LeggedLoginAPI() {
    return preLoginRetrofit().create(Settrade3LeggedLoginAPI.class);
  }

  @Bean
  public SettradeUserAPI userAPI() {
    return postLoginRetrofit().create(SettradeUserAPI.class);
  }

  @Bean
  public SettradeStreamAPI streamAPI() {
    return postLoginRetrofit().create(SettradeStreamAPI.class);
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
