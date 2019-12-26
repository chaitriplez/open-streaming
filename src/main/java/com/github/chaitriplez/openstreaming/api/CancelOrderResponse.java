package com.github.chaitriplez.openstreaming.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderResponse {
  Long orderNo;
  String httpStatus;
  int httpStatusCode;
  ErrorResponse errorResponse;
}
