package com.github.chaitriplez.openstreaming.component;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Tuple<A, B> {
  private A first;
  private B second;
}
