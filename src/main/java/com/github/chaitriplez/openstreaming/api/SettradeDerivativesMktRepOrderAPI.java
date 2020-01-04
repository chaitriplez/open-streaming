package com.github.chaitriplez.openstreaming.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SettradeDerivativesMktRepOrderAPI {

  @POST("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  Call<PlaceOrderResponse> placeOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Body PlaceOrderRequest placeOrderRequest);

  @PATCH("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders/{orderNo}/cancel")
  Call<Void> cancelOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Path("orderNo") Long orderNo);

  @PATCH("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/cancel")
  Call<CancelMultipleOrdersResponse> cancelMultipleOrders(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Body List<Long> orders);

  @PATCH("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders/{orderNo}/change")
  Call<Void> changeOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Path("orderNo") Long orderNo,
      @Body MktRepChangeOrderRequest changeRequest);
}
