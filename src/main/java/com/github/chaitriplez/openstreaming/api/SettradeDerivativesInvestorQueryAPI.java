package com.github.chaitriplez.openstreaming.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SettradeDerivativesInvestorQueryAPI {

  @GET("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders")
  Call<List<OrderResponse>> listOrder(
      @Path("brokerId") String brokerId, @Path("accountNo") String accountNo);

  @GET("/api/seosd/v1/{brokerId}/accounts/{accountNo}/orders/{orderNo}")
  Call<OrderResponse> getOrder(
      @Path("brokerId") String brokerId,
      @Path("accountNo") String accountNo,
      @Path("orderNo") Long orderNo);

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
