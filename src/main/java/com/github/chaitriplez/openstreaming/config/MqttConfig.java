package com.github.chaitriplez.openstreaming.config;

import com.github.chaitriplez.openstreaming.util.AuthorizationSupplier;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Setter
@EnableConfigurationProperties({OpenStreamingProperties.class, MqttProperties.class})
@Configuration
public class MqttConfig {

  private static final String STREAM_URI = "%s/api/dispatcher/v1/%s/mqtt";

  @Autowired private OpenStreamingProperties osProp;
  @Autowired private MqttProperties mqttProp;
  @Autowired private AuthorizationSupplier authorization;

  private static SocketFactory ignoreCert() {
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
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      log.warn("Cannot config insecure connection.", e);
      return null;
    }
  }

  @Bean
  public IMqttClient mqttClient() throws MqttException, URISyntaxException {
    URI uri = new URI(String.format(STREAM_URI, mqttProp.getStreamHost(), osProp.getBrokerId()));
    IMqttClient client = new MqttClient(uri.normalize().toString(), "", new MemoryPersistence());
    return client;
  }

  @Bean
  public MqttConnectOptions mqttConnectOptions() {
    MqttConnectOptions conOpt = new MqttConnectOptions();
    conOpt.setCleanSession(true);
    conOpt.setAutomaticReconnect(true);
    conOpt.setKeepAliveInterval(mqttProp.getKeepAliveInterval());
    conOpt.setCustomWebSocketHeaders(authorizationHeader());

    if (mqttProp.isHttpInsecure()) {
      conOpt.setSocketFactory(ignoreCert());
      conOpt.setHttpsHostnameVerificationEnabled(false);
    }

    return conOpt;
  }

  private Properties authorizationHeader() {
    Properties properties =
        new Properties() {
          @Override
          public String getProperty(String key) {
            if (key.equals("Authorization")) {
              return authorization.getAuthorization();
            }
            return super.getProperty(key);
          }
        };
    properties.put("Authorization", "");
    return properties;
  }
}
