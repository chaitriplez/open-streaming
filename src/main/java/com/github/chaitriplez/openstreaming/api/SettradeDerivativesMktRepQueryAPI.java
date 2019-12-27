package com.github.chaitriplez.openstreaming.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SettradeDerivativesMktRepQueryAPI {

  @GET("/api/seosd/v1/{brokerId}/mktrep/accounts/{accountNo}/orders")
  Call<List<OrderResponse>> listOrder(
      @Path("brokerId") String brokerId, @Path("accountNo") String accountNo);

  @GET("/api/seosd/v1/{brokerId}/mktrep/orders/{orderNo}")
  Call<OrderResponse> getOrder(@Path("brokerId") String brokerId, @Path("orderNo") String orderNo);
}
