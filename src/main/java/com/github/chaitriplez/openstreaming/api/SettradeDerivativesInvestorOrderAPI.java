package com.github.chaitriplez.openstreaming.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SettradeDerivativesInvestorOrderAPI {

  @POST("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders")
  Call<PlaceOrderResponse> placeOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Body InvestorPlaceOrderRequest placeOrderRequest);

  @PATCH("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders/{orderNo}/cancel")
  Call<Void> cancelOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Path("orderNo") Long orderNo,
      @Body InvestorCancelOrderRequest cancelRequest);

  @PATCH("/api/seosd/v1/{brokerId}/accounts/{accountNo}/cancel")
  Call<CancelMultipleOrdersResponse> cancelMultipleOrders(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Body CancelMultipleOrderRequest cancelRequest);

  @PATCH("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders/{orderNo}/change")
  Call<Void> changeOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Path("orderNo") Long orderNo,
      @Body InvestorChangeOrderRequest changeRequest);
}
