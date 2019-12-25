package com.github.chaitriplez.openstreaming.config;

import com.github.chaitriplez.openstreaming.api.Settrade2LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.Settrade3LeggedLoginAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepAPI;
import com.github.chaitriplez.openstreaming.api.SettradeUserAPI;
import com.github.chaitriplez.openstreaming.util.AccessTokenInterceptor;
import com.github.chaitriplez.openstreaming.util.AccessTokenSupplier;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
@EnableConfigurationProperties(OpenStreamingProperties.class)
@Configuration
public class APIConfig {

  @Bean
  @ConditionalOnProperty(prefix = "openstreaming", name = "login-type", havingValue = "TWO_LEGGED")
  public Settrade2LeggedLoginAPI settrade2LeggedLoginAPI(OpenStreamingProperties prop) {
    return new Retrofit.Builder()
        .baseUrl(prop.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create())
        .build()
        .create(Settrade2LeggedLoginAPI.class);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "openstreaming",
      name = "login-type",
      havingValue = "THREE_LEGGED")
  public Settrade3LeggedLoginAPI settrade3LeggedLoginAPI(OpenStreamingProperties prop) {
    return new Retrofit.Builder()
        .baseUrl(prop.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create())
        .build()
        .create(Settrade3LeggedLoginAPI.class);
  }

  @Bean
  public AccessTokenSupplier accessToken() {
    return new AccessTokenSupplier();
  }

  @Bean
  public SettradeUserAPI userAPI(OpenStreamingProperties prop, AccessTokenSupplier accessToken) {
    return new Retrofit.Builder()
        .baseUrl(prop.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create())
        .client(
            new OkHttpClient()
                .newBuilder()
                .addInterceptor(new AccessTokenInterceptor(accessToken))
                .build())
        .build()
        .create(SettradeUserAPI.class);
  }

  @Bean
  @ConditionalOnProperty(prefix = "openstreaming", name = "user-type", havingValue = "MKT_REP")
  public SettradeDerivativesMktRepAPI mktRepAPI(
      OpenStreamingProperties prop, AccessTokenSupplier accessToken) {
    return new Retrofit.Builder()
        .baseUrl(prop.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create())
        .client(
            new OkHttpClient()
                .newBuilder()
                .addInterceptor(new AccessTokenInterceptor(accessToken))
                .build())
        .build()
        .create(SettradeDerivativesMktRepAPI.class);
  }

  @Bean
  @ConditionalOnProperty(prefix = "openstreaming", name = "user-type", havingValue = "INVESTOR")
  public SettradeDerivativesInvestorAPI investorAPI(
      OpenStreamingProperties prop, AccessTokenSupplier accessToken) {
    return new Retrofit.Builder()
        .baseUrl(prop.getApiHost())
        .addConverterFactory(JacksonConverterFactory.create())
        .client(
            new OkHttpClient()
                .newBuilder()
                .addInterceptor(new AccessTokenInterceptor(accessToken))
                .build())
        .build()
        .create(SettradeDerivativesInvestorAPI.class);
  }
}
