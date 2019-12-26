package com.github.chaitriplez.openstreaming.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SettradeDerivativesInvestorAPI {

  @GET("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders")
  Call<List<OrderResponse>> listOrder(
      @Path("brokerId") String brokerId, @Path("accountNo") String accountNo);

  @GET("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders/{orderNo}")
  Call<OrderResponse> getOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Path("orderNo") Long orderNo);

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

  @GET("/api/seosd/v1/{brokerId}/accounts/{accountNo}/account-info")
  Call<AccountInfoResponse> getAccountInfo(
      @Path("brokerId") String brokerId, @Path("accountNo") String accountNo);

  @GET("/api/seosd/v1/{brokerId}/accounts/{accountNo}/portfolios")
  Call<List<PortfolioResponse>> getPortfolio(
      @Path("brokerId") String brokerId, @Path("accountNo") String accountNo);

  @GET("/api/seosd/v1/{brokerId}/accounts/{accountNo}/trades")
  Call<List<TradeResponse>> listTrade(
      @Path("brokerId") String brokerId, @Path("accountNo") String accountNo);
}
