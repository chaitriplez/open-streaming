package com.github.chaitriplez.openstreaming.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SettradeDerivativesMktRepAPI {

  @GET("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  Call<List<OrderResponse>> listOrder(
      @Path("brokerId") String brokerId, @Path("accountNo") String accountNo);

  @GET("/api/seosd/v1/{brokerId}/mktrep/orders/{orderNo}")
  Call<OrderResponse> getOrder(@Path("brokerId") String brokerId, @Path("orderNo") String orderNo);

  @POST("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  Call<PlaceOrderResponse> placeOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Body PlaceOrderRequest placeOrderRequest);

  @PATCH("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders/{orderNo}/cancel")
  Call<Void> cancelOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Path("orderNo") String orderNo);
}
