package com.github.chaitriplez.openstreaming.service.task;

import com.github.chaitriplez.openstreaming.repository.OrderCache;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCacheCondition {

  private Long orderNo;

  private Predicate<OrderCache> predicate;
}
