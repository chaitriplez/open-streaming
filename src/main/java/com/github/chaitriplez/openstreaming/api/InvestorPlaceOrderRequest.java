package com.github.chaitriplez.openstreaming.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InvestorPlaceOrderRequest extends PlaceOrderRequest {
  String pin;
}
