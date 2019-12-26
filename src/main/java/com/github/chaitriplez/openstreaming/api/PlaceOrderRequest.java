package com.github.chaitriplez.openstreaming.api;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {
  Position position;
  LongShort side;
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
