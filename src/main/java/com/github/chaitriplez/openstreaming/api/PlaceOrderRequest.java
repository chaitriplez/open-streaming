package com.github.chaitriplez.openstreaming.api;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {
  Position position;
  Side side;
  String symbol;
  Integer volume;
  PriceType priceType;
  BigDecimal price;

  ValidityType validityType;

  StopCondition stopCondition;
  BigDecimal stopPrice;
  String stopSymbol;

  boolean bypassWarning;
}
