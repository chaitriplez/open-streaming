package com.github.chaitriplez.openstreaming.service;

import java.util.List;

public interface AsyncOrderService {

  /**
   * @param requests
   * @return jobId
   */
  Long limitOrder(List<LimitOrderRequest> requests);

  /**
   * Cancel active order in order cache
   *
   * @return jobId
   */
  Long cancelAllOrder();

  /**
   * Cancel active order in order cache by symbol
   *
   * @param symbol
   * @return jobId
   */
  Long cancelOrderBySymbol(String symbol);
}
