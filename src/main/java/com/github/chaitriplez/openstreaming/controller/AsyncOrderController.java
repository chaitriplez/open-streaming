package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.service.AsyncOrderService;
import com.github.chaitriplez.openstreaming.service.ChangePxQtyRequest;
import com.github.chaitriplez.openstreaming.service.LimitOrderRequest;
import com.github.chaitriplez.openstreaming.service.QuoteRequest;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Setter
@RestController
public class AsyncOrderController {

  @Autowired private AsyncOrderService asyncOrderService;

  @PostMapping("/api-os/async-order/v1/limit")
  public Long limitOrder(@RequestBody List<LimitOrderRequest> requests) {
    return asyncOrderService.limitOrder(requests);
  }

  @PostMapping("/api-os/async-order/v1/cancel")
  public Long cancelOrder(@RequestBody List<Long> orderNos) {
    return asyncOrderService.cancelOrder(orderNos);
  }

  @PostMapping("/api-os/async-order/v1/cancelAll")
  public Long cancelAllOrder() {
    return asyncOrderService.cancelAllOrder();
  }

  @PostMapping("/api-os/async-order/v1/cancelBySymbol")
  public Long cancelOrderBySymbol(@RequestParam("symbol") String symbol) {
    return asyncOrderService.cancelOrderBySymbol(symbol);
  }

  @PostMapping("/api-os/async-order/v1/change")
  public Long changeOrder(@RequestBody List<ChangePxQtyRequest> requests) {
    return asyncOrderService.changePxQty(requests);
  }

  @PostMapping("/api-os/async-order/v1/quote")
  public Long quoteRequest(
      @RequestParam("symbol") String symbol, @RequestBody List<QuoteRequest> requests) {
    return asyncOrderService.quote(symbol, requests);
  }
}
