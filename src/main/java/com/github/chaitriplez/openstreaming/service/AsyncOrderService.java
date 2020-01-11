package com.github.chaitriplez.openstreaming.service;

import java.util.List;

public interface AsyncOrderService {

  /**
   * @param requests
   * @return jobId
   */
  Long limitOrder(List<LimitOrderRequest> requests);

  /**
   * @param orderNos
   * @return jobId
   */
  Long cancelOrder(List<Long> orderNos);

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

  /**
   * @param requests
   * @return jobId
   */
  Long changePxQty(List<ChangePxQtyRequest> requests);

  /**
   * Replace current orders(cancel, change, place new order) with new quotes request.
   *
   * @param symbol
   * @param quotes
   * @return
   */
  Long quote(String symbol, List<QuoteRequest> quotes);
}
