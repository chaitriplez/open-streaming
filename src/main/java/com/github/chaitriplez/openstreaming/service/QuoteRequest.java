package com.github.chaitriplez.openstreaming.service;

import com.github.chaitriplez.openstreaming.api.LongShort;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequest {
  LongShort side;
  BigDecimal px;
  Integer qty;
}
