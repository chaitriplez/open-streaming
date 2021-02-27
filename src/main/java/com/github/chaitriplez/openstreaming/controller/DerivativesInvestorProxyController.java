package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.api.AccountInfoResponse;
import com.github.chaitriplez.openstreaming.api.CancelMultipleOrderRequest;
import com.github.chaitriplez.openstreaming.api.CancelMultipleOrdersResponse;
import com.github.chaitriplez.openstreaming.api.InvestorCancelOrderRequest;
import com.github.chaitriplez.openstreaming.api.InvestorChangeOrderRequest;
import com.github.chaitriplez.openstreaming.api.InvestorPlaceOrderRequest;
import com.github.chaitriplez.openstreaming.api.OrderResponse;
import com.github.chaitriplez.openstreaming.api.PlaceOrderResponse;
import com.github.chaitriplez.openstreaming.api.PortfolioResponse;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorOrderAPI;
import com.github.chaitriplez.openstreaming.api.SettradeDerivativesInvestorQueryAPI;
import com.github.chaitriplez.openstreaming.api.TradeResponse;
import com.github.chaitriplez.openstreaming.util.ResponseEntityConverter;
import java.util.List;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Setter
@RestController
public class DerivativesInvestorProxyController {

  @Autowired private SettradeDerivativesInvestorQueryAPI queryApi;
  @Autowired private SettradeDerivativesInvestorOrderAPI orderApi;

  @GetMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders")
  ResponseEntity<List<OrderResponse>> listOrder(
      @PathVariable("brokerId") String brokerId, @PathVariable("accountNo") String accountNo) {
    return ResponseEntityConverter.from(queryApi.listOrder(brokerId, accountNo));
  }

  @GetMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders/{orderNo}")
  ResponseEntity<OrderResponse> getOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @PathVariable("orderNo") Long orderNo) {
    return ResponseEntityConverter.from(queryApi.getOrder(brokerId, accountNo, orderNo));
  }

  @GetMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/account-info")
  ResponseEntity<AccountInfoResponse> getAccountInfo(
      @PathVariable("brokerId") String brokerId, @PathVariable("accountNo") String accountNo) {
    return ResponseEntityConverter.from(queryApi.getAccountInfo(brokerId, accountNo));
  }

  @GetMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/portfolios")
  ResponseEntity<List<PortfolioResponse>> getPortfolio(
      @PathVariable("brokerId") String brokerId, @PathVariable("accountNo") String accountNo) {
    return ResponseEntityConverter.from(queryApi.getPortfolio(brokerId, accountNo));
  }

  @GetMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/trades")
  ResponseEntity<List<TradeResponse>> listTrade(
      @PathVariable("brokerId") String brokerId, @PathVariable("accountNo") String accountNo) {
    return ResponseEntityConverter.from(queryApi.listTrade(brokerId, accountNo));
  }

  @PostMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders")
  ResponseEntity<PlaceOrderResponse> placeOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @RequestBody InvestorPlaceOrderRequest placeOrderRequest) {
    return ResponseEntityConverter.from(
        orderApi.placeOrder(brokerId, accountNo, placeOrderRequest));
  }

  @PatchMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders/{orderNo}/cancel")
  ResponseEntity<Void> cancelOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @PathVariable("orderNo") Long orderNo,
      @RequestBody InvestorCancelOrderRequest cancelRequest) {
    return ResponseEntityConverter.from(
        orderApi.cancelOrder(brokerId, accountNo, orderNo, cancelRequest));
  }

  @PatchMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/cancel")
  ResponseEntity<CancelMultipleOrdersResponse> cancelMultipleOrders(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @RequestBody CancelMultipleOrderRequest cancelRequest) {
    return ResponseEntityConverter.from(
        orderApi.cancelMultipleOrders(brokerId, accountNo, cancelRequest));
  }

  @PatchMapping("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders/{orderNo}/change")
  ResponseEntity<Void> changeOrder(
      @PathVariable("brokerId") String brokerId,
      @PathVariable("accountNo") String accountNo,
      @PathVariable("orderNo") Long orderNo,
      @RequestBody InvestorChangeOrderRequest changeRequest) {
    return ResponseEntityConverter.from(
        orderApi.changeOrder(brokerId, accountNo, orderNo, changeRequest));
  }
}
