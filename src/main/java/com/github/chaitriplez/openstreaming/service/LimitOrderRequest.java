package com.github.chaitriplez.openstreaming.service;

import com.github.chaitriplez.openstreaming.api.LongShort;
import com.github.chaitriplez.openstreaming.api.Position;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitOrderRequest {
  private Position position;
  private LongShort side;
  private String symbol;
  private BigDecimal px;
  private Integer qty;
}
