package com.github.chaitriplez.openstreaming.api;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {
  String brokerId;
  String accountNo;
  String symbol;
  String underlying;
  String securityType;
  LocalDate lastTradingDate;
  BigDecimal multiplier;
  String currency;
  BigDecimal currentXRT;
  Timestamp asOfDateXRT;
  Boolean hasLongPosition;
  Integer startLongPosition;
  Integer actualLongPosition;
  Integer availableLongPosition;
  BigDecimal startLongPrice;
  BigDecimal startLongCost;
  BigDecimal longAvgPrice;
  BigDecimal longAvgCost;
  Integer openLongPosition;
  Integer closeLongPosition;
  BigDecimal startXRTLong;
  BigDecimal startXRTLongCost;
  BigDecimal avgXRTLong;
  BigDecimal avgXRTLongCost;
  Boolean hasShortPosition;
  Integer startShortPosition;
  Integer actualShortPosition;
  Integer availableShortPosition;
  BigDecimal startShortPrice;
  BigDecimal startShortCost;
  BigDecimal shortAvgPrice;
  BigDecimal shortAvgCost;
  Integer openShortPosition;
  Integer closeShortPosition;
  BigDecimal startXRTShort;
  BigDecimal startXRTShortCost;
  BigDecimal avgXRTShort;
  BigDecimal avgXRTShortCost;
  BigDecimal marketPrice;
  BigDecimal realizedPL;
  BigDecimal realizedPLByCost;
  BigDecimal realizedPLCurrency;
  BigDecimal realizedPLByCostCurrency;
}
