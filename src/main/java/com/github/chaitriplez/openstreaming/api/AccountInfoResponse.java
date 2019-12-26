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
public class AccountInfoResponse {
  String callForceFlag;
  BigDecimal callForceMargin;
  BigDecimal cashBalance;
  BigDecimal creditLine;
  BigDecimal equity;
  BigDecimal excessEquity;
  BigDecimal totalMM;
  BigDecimal totalMR;
}
