package com.github.chaitriplez.openstreaming.api.line;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushRequest {
  String to;
  @Singular List<Message> messages;
  @Builder.Default boolean notificationDisabled = false;
}
