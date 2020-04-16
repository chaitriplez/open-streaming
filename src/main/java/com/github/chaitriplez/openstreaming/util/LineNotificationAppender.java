package com.github.chaitriplez.openstreaming.util;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.chaitriplez.openstreaming.api.line.MessageAPI;
import com.github.chaitriplez.openstreaming.api.line.PushRequest;
import com.github.chaitriplez.openstreaming.api.line.SimpleTextMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LineNotificationAppender extends AppenderBase<ILoggingEvent> {

  private final Callback<JsonNode> callback =
      new Callback<JsonNode>() {
        @Override
        public void onResponse(Call<JsonNode> call, Response<JsonNode> response) {
          if (!response.isSuccessful()) {
            System.out.println(
                "Request to "
                    + call.request().url()
                    + " error with code: "
                    + response.code()
                    + ", message: "
                    + response.message());
          }
        }

        @Override
        public void onFailure(Call<JsonNode> call, Throwable t) {
          System.out.println("Request to " + call.request().url() + " error with exception.");
          t.printStackTrace(System.out);
        }
      };

  PatternLayoutEncoder encoder;
  boolean enabled;
  String userId;
  String channelAccessToken;

  MessageAPI messageAPI;

  @Override
  public void start() {
    if (!enabled) {
      addInfo("Disable appender named [" + name + "].");
      return;
    }

    {
      boolean error = false;
      if (this.encoder == null) {
        addError("No encoder set for the appender named [" + name + "].");
        error = true;
      }
      if (this.userId == null) {
        addError("No user id set for the appender named [" + name + "].");
        error = true;
      }
      if (this.channelAccessToken == null) {
        addError("No channel access set for the appender named [" + name + "].");
        error = true;
      }
      if (error) {
        return;
      }
    }

    {
      Retrofit retrofit =
          new Retrofit.Builder()
              .baseUrl("https://api.line.me/")
              .addConverterFactory(JacksonConverterFactory.create())
              .build();

      messageAPI = retrofit.create(MessageAPI.class);
    }

    super.start();
  }

  @Override
  public void append(ILoggingEvent event) {
    PushRequest request =
        PushRequest.builder()
            .to(userId)
            .message(new SimpleTextMessage(new String(this.encoder.encode(event))))
            .build();
    Call<JsonNode> call = messageAPI.pushMessage("Bearer " + channelAccessToken, request);
    call.enqueue(callback);
  }

  public PatternLayoutEncoder getEncoder() {
    return encoder;
  }

  public void setEncoder(PatternLayoutEncoder encoder) {
    this.encoder = encoder;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getChannelAccessToken() {
    return channelAccessToken;
  }

  public void setChannelAccessToken(String channelAccessToken) {
    this.channelAccessToken = channelAccessToken;
  }
}
