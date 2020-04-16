package com.github.chaitriplez.openstreaming.api.line;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleTextMessage implements Message {
  final String type = "text";
  String text;

  public SimpleTextMessage(String text) {
    this.text = text;
  }
}
