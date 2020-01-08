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
public class ChangeOrderRequest {
  Position newPosition;
  BigDecimal newPrice;
  Integer newVolume;
  PriceType newPriceType;
}
