package com.github.chaitriplez.openstreaming.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  String code;
  String message;

  @Singular("additionalInfo")
  Map<String, JsonNode> additionalInfo;
}
