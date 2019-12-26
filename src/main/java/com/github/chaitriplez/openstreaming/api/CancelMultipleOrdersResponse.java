package com.github.chaitriplez.openstreaming.api;

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
public class CancelMultipleOrdersResponse {
  @Singular List<CancelOrderResponse> results;
}
