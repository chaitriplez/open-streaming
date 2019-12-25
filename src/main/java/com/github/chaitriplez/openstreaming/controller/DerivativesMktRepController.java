package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.api.OrderResponse;
import com.github.chaitriplez.openstreaming.api.PlaceOrderRequest;
import com.github.chaitriplez.openstreaming.api.PlaceOrderResponse;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesMktRepAPI;
import com.github.chaitriplez.openstreaming.util.ResponseEntityConverter;
import java.util.List;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Setter
@ConditionalOnBean(SettradeDerivativesMktRepAPI.class)
@RestController
public class DerivativesMktRepController {

  @Autowired private SettradeDerivativesMktRepAPI api;

  @GetMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  ResponseEntity<List<OrderResponse>> listOrder(
      @PathVariable("brokerId") String brokerId, @PathVariable("accountNo") String accountNo) {
    return ResponseEntityConverter.from(api.listOrder(brokerId, accountNo));
  }

  @GetMapping("/api/seosd/v1/{brokerId}/mktrep/orders/{orderNo}")
  ResponseEntity<OrderResponse> getOrder(
      @PathVariable("brokerId") String brokerId, @PathVariable("orderNo") String orderNo) {
    return ResponseEntityConverter.from(api.getOrder(brokerId, orderNo));
  }

  @PostMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  ResponseEntity<PlaceOrderResponse> placeOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @RequestBody PlaceOrderRequest placeOrderRequest) {
    return ResponseEntityConverter.from(api.placeOrder(brokerId, accountNo, placeOrderRequest));
  }

  @PatchMapping("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders/{orderNo}/cancel")
  ResponseEntity<Void> cancelOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @PathVariable("orderNo") String orderNo) {
    return ResponseEntityConverter.from(api.cancelOrder(brokerId, accountNo, orderNo));
  }
}
