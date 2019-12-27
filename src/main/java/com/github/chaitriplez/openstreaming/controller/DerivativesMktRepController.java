package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.api.CancelMultipleOrdersResponse;
import com.github.chaitriplez.openstreaming.api.MktRepChangeOrderRequest;
import com.github.chaitriplez.openstreaming.api.OrderResponse;
import com.github.chaitriplez.openstreaming.api.PlaceOrderRequest;
import com.github.chaitriplez.openstreaming.api.PlaceOrderResponse;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepQueryAPI;
import com.github.chaitriplez.openstreaming.api.TradeResponse;
import com.github.chaitriplez.openstreaming.util.ResponseEntityConverter;
import java.util.List;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.Body;
import retrofit2.http.Path;

@Setter
@RestController
@ConditionalOnProperty(prefix = "openstreaming", name = "user-type", havingValue = "MKT_REP")
public class DerivativesMktRepController {

  @Autowired private SettradeDerivativesMktRepQueryAPI queryApi;
  @Autowired private SettradeDerivativesMktRepOrderAPI orderApi;

  @GetMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  ResponseEntity<List<OrderResponse>> listOrder(
      @PathVariable("brokerId") String brokerId, @PathVariable("accountNo") String accountNo) {
    return ResponseEntityConverter.from(queryApi.listOrder(brokerId, accountNo));
  }

  @GetMapping("/api/seosd/v1/{brokerId}/mktrep/orders/{orderNo}")
  ResponseEntity<OrderResponse> getOrder(
      @PathVariable("brokerId") String brokerId, @PathVariable("orderNo") String orderNo) {
    return ResponseEntityConverter.from(queryApi.getOrder(brokerId, orderNo));
  }

  @GetMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/trades")
  ResponseEntity<List<TradeResponse>> listTrade(
      @PathVariable("brokerId") String brokerId, @PathVariable("accountNo") String accountNo) {
    return ResponseEntityConverter.from(queryApi.listTrade(brokerId, accountNo));
  }

  @PostMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  ResponseEntity<PlaceOrderResponse> placeOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @RequestBody PlaceOrderRequest placeOrderRequest) {
    return ResponseEntityConverter.from(
        orderApi.placeOrder(brokerId, accountNo, placeOrderRequest));
  }

  @PatchMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders/{orderNo}/cancel")
  ResponseEntity<Void> cancelOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @PathVariable("orderNo") String orderNo) {
    return ResponseEntityConverter.from(orderApi.cancelOrder(brokerId, accountNo, orderNo));
  }

  @PatchMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/cancel")
  ResponseEntity<CancelMultipleOrdersResponse> cancelMultipleOrders(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Body List<Long> orders) {
    return ResponseEntityConverter.from(orderApi.cancelMultipleOrders(brokerId, accountNo, orders));
  }

  @PatchMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders/{orderNo}/change")
  ResponseEntity<Void> changeOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @PathVariable("orderNo") Long orderNo,
      @RequestBody MktRepChangeOrderRequest changeRequest) {
    return ResponseEntityConverter.from(
        orderApi.changeOrder(brokerId, accountNo, orderNo, changeRequest));
  }
}
