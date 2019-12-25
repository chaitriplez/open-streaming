package com.github.chaitriplez.openstreaming.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
  Long orderNo;
  String tfxOrderNo;
  String accountNo;

  String entryId;
  String entryDate;
  String entryTime;

  String position;
  String side;
  String symbol;
  Integer qty;
  String priceType;
  BigDecimal price;
  String validity;

  Integer matchQty;
  Integer balanceQty;
  Integer cancelQty;

  String isStopOrderNotActivate;
  StopCondition triggerCondition;
  String triggerSymbol;
  BigDecimal triggerPrice;

  String status;
  String showStatus;
  String statusMeaning;

  Integer rejectCode;
  String rejectReason;

  Long version;

  public LocalDate toEntryDate() {
    return LocalDate.parse(entryDate, DateTimeFormatter.ofPattern("dd/MM/yy"));
  }

  public LocalTime toEntryTime() {
    return LocalTime.parse(entryId, DateTimeFormatter.ISO_TIME);
  }
}
