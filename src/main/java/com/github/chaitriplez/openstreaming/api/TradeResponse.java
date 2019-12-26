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
public class TradeResponse {
  String brokerId;
  Long orderNo;
  LocalDate tradeDate;
  String entryId;
  String accountNo;
  String tradeNo;
  String dealNo;
  Timestamp tradeTime;
  String symbol;
  BuySell side;
  Integer qty;
  BigDecimal px;
  Position openClose;
  String status;
  String tradeType;
  Integer rectifiedQty;
  BigDecimal multiplier;
  String currency;
  LocalDate ledgerDate;
  Integer ledgerSeq;
  Timestamp ledgerTime;
  LocalDate refLedgerDate;
  Integer refLedgerSeq;
  String rejectCode;
  String rejectReason;
}
