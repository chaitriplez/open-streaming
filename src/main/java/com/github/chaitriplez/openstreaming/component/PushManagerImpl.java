package com.github.chaitriplez.openstreaming.component;

import com.github.chaitriplez.openstreaming.api.PreConnectStreamResponse;
import com.github.chaitriplez.openstreaming.api.SettradeStreamAPI;
import com.github.chaitriplez.openstreaming.config.OpenStreamingProperties;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

@Setter
@Slf4j
@EnableConfigurationProperties(PushManagerProperties.class)
@Component
public class PushManagerImpl implements PushManager {
  private static final String STREAM_URI = "wss://%s/api/dispatcher/v1/%s/mqtt";
  private final ConcurrentMap<String, Set<PushListener>> subscribers = new ConcurrentHashMap<>();

  private IMqttClient client = null;
  private boolean reconnecting = false;
  private int reconnectCount = 0;
  private ScheduledExecutorService reconnectService = Executors.newSingleThreadScheduledExecutor();

  @Autowired private SettradeStreamAPI streamAPI;
  @Autowired private OpenStreamingProperties osProp;
  @Autowired private PushManagerProperties pushProp;

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

  @PostConstruct
  public void destroy() {
    reconnectService.shutdownNow();
    if (isClientConnected()) {
      doStop();
    }
  }

  @Override
  public synchronized void start() throws Exception {
    if (reconnecting) {
      throw new IllegalStateException("Reconnecting stream service.");
    }
    if (isClientConnected()) {
      throw new IllegalStateException("Already connect stream service.");
    }
    doStart();
  }

  private boolean isClientConnected() {
    return client != null && client.isConnected();
  }

  private void reconnect() {
    if (!pushProp.isAutoReconnect()) {
      reconnecting = false;
      reconnectCount = 0;
      subscribers.clear();
      client = null;
      return;
    }
    if (reconnectCount >= pushProp.getMaximumReconnect()) {
      log.error("Cannot reconnect stream service. Please manual connect and subscribe.");
      reconnecting = false;
      reconnectCount = 0;
      subscribers.clear();
      client = null;
      return;
    }
    reconnecting = true;
    reconnectCount++;
    reconnectService.schedule(
        () -> {
          try {
            log.info("Reconnecting stream service count[{}]", reconnectCount);
            doStart();
            subscribers
                .keySet()
                .forEach(
                    s -> {
                      try {
                        log.debug("Resubscribe topic[{}]", s);
                        client.subscribe(s, 0);
                      } catch (MqttException e) {
                        log.error("Resubscribe fail topic[{}]", s, e);
                      }
                    });
            reconnecting = false;
            reconnectCount = 0;
          } catch (Exception e) {
            log.warn("Cannot reconnect stream service count[{}]", reconnectCount, e);
            reconnect();
          }
        },
        pushProp.getReconnectInterval(),
        TimeUnit.SECONDS);
  }

  private void doStart() throws Exception {
    Call<PreConnectStreamResponse> call = streamAPI.preConnect(osProp.getBrokerId());
    Response<PreConnectStreamResponse> response = call.execute();
    if (!response.isSuccessful()) {
      String error = response.errorBody().string();
      log.warn("Cannot get stream host: {}", error);
      throw new RuntimeException("Cannot get stream host: " + error);
    }

    PreConnectStreamResponse preConnectResponse = response.body();
    String token = preConnectResponse.getToken();
    List<String> uris =
        preConnectResponse.getHosts().stream()
            .map(s -> String.format(STREAM_URI, s, osProp.getBrokerId()))
            .collect(Collectors.toList());
    Collections.shuffle(uris);

    client = new MqttClient(uris.get(0), "", new MemoryPersistence());
    client.setCallback(
        new MqttCallbackExtended() {
          @Override
          public void connectComplete(boolean reconnect, String serverURI) {
            log.info("Connect[{}] completed!", serverURI);
          }

          @Override
          public void connectionLost(Throwable cause) {
            log.warn("connectionLost", cause);
            reconnect();
          }

          @Override
          public void messageArrived(String topic, MqttMessage message) {
            log.debug("messageArrived {}", message.toString());
            subscribers
                .getOrDefault(topic, Collections.emptySet())
                .forEach(pushListener -> pushListener.receive(message.getPayload()));
          }

          @Override
          public void deliveryComplete(IMqttDeliveryToken token) {
            log.debug("deliveryComplete {}", token);
          }
        });

    MqttConnectOptions conOpt = new MqttConnectOptions();
    conOpt.setServerURIs(uris.toArray(new String[0]));
    conOpt.setCleanSession(true);
    conOpt.setAutomaticReconnect(false);
    conOpt.setKeepAliveInterval(pushProp.getKeepAliveInterval());
    Properties headers = new Properties();
    headers.put("Authorization", "Bearer " + token);
    conOpt.setCustomWebSocketHeaders(headers);
    if (pushProp.isHttpInsecure()) {
      conOpt.setSocketFactory(ignoreCert());
      conOpt.setHttpsHostnameVerificationEnabled(false);
    }
    log.info("Connecting {}", uris);
    client.connect(conOpt);
  }

  @Override
  public synchronized void stop() {
    if (!isClientConnected()) {
      log.warn("SKIP: Client has already disconnected!");
      return;
    }
    doStop();
  }

  private void doStop() {
    try {
      log.info("Disconnecting {}", client.getServerURI());
      client.disconnect();
      subscribers.clear();
    } catch (MqttException e) {
      log.warn("Cannot disconnect push", e);
    }
  }

  @Override
  public synchronized boolean isConnected() {
    return isClientConnected();
  }

  @Override
  public synchronized void subscribe(String topic, PushListener listener) throws MqttException {
    if (!isClientConnected()) {
      throw new IllegalStateException("Stream service was stopped.");
    }
    if (!subscribers.containsKey(topic)) {
      log.info("Subscribe topic[{}]", topic);
      client.subscribe(topic, 0);
      subscribers.put(topic, ConcurrentHashMap.newKeySet());
    }
    subscribers.computeIfPresent(
        topic,
        (s, pushListeners) -> {
          log.debug("Add listener[{}]", listener);
          pushListeners.add(listener);
          return pushListeners;
        });
  }

  @Override
  public synchronized void unsubscribe(String topic, PushListener listener) throws Exception {
    if (!isClientConnected()) {
      throw new IllegalStateException("Stream service was stopped.");
    }
    if (subscribers.computeIfPresent(
            topic,
            (s, pushListeners) -> {
              log.debug("Remove listener[{}]", listener);
              pushListeners.remove(listener);
              if (pushListeners.isEmpty()) {
                return null;
              }
              return pushListeners;
            })
        == null) {
      log.info("Unsubscribe topic[{}]", topic);
      client.unsubscribe(topic);
    }
  }

  @Override
  public synchronized void unsubscribe(String topic) throws MqttException {
    if (!isClientConnected()) {
      throw new IllegalStateException("Stream service was stopped.");
    }
    log.debug("Remove all listener from topic[{}]");
    subscribers.remove(topic);
    log.info("Unsubscribe topic[{}]");
    client.unsubscribe(topic);
  }
}
