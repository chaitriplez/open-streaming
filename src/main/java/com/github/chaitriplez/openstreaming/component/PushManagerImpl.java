package com.github.chaitriplez.openstreaming.component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Slf4j
@Component
public class PushManagerImpl implements PushManager {

  private final ConcurrentMap<String, Set<PushListener>> subscribers = new ConcurrentHashMap<>();

  @Autowired private IMqttClient client;
  @Autowired private MqttConnectOptions connectOptions;

  @PostConstruct
  public void init() {
    client.setCallback(
        new MqttCallbackExtended() {
          @Override
          public void connectComplete(boolean reconnect, String serverURI) {
            if (reconnect) {
              log.info("Reconnect[{}] completed!", serverURI);
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
            } else {
              log.info("Connect[{}] completed!", serverURI);
            }
          }

          @Override
          public void connectionLost(Throwable cause) {
            log.warn("connectionLost", cause);
          }

          @Override
          public void messageArrived(String topic, MqttMessage message) {
            subscribers
                .getOrDefault(topic, Collections.emptySet())
                .forEach(pushListener -> pushListener.receive(message.getPayload()));
          }

          @Override
          public void deliveryComplete(IMqttDeliveryToken token) {
            log.debug("deliveryComplete {}", token);
          }
        });
  }

  @Override
  public void start() throws MqttException {
    if (client.isConnected()) {
      log.warn("SKIP: Client has already connected!");
      return;
    }
    log.info("Connecting {}", client.getServerURI());
    client.connect(connectOptions);
  }

  @PreDestroy
  @Override
  public void stop() {
    if (!client.isConnected()) {
      log.warn("SKIP: Client has already disconnected!");
      return;
    }
    try {
      log.info("Disconnecting {}", client.getServerURI());
      client.disconnect();
      subscribers.clear();
    } catch (MqttException e) {
      log.warn("Cannot disconnect push", e);
    }
  }

  @Override
  public void subscribe(String topic, PushListener listener) throws MqttException {
    if (!subscribers.containsKey(topic)) {
      log.debug("Subscribe topic[{}]", topic);
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
  public void unsubscribe(String topic, PushListener listener) throws Exception {
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
      log.debug("Unsubscribe topic[{}]", topic);
      client.unsubscribe(topic);
    }
  }

  @Override
  public void unsubscribe(String topic) throws MqttException {
    log.debug("Unsubscribe topic[{}]");
    subscribers.remove(topic);
    log.debug("Remove all listener from topic[{}]");
    client.unsubscribe(topic);
  }
}
