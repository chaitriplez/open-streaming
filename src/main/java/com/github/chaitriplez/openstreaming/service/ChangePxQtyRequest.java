package com.github.chaitriplez.openstreaming.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePxQtyRequest {
  private Long orderNo;
  private boolean changePx;
  private BigDecimal px;
  private boolean changeQty;
  private Integer qty;
}
