package com.example.s2o_mobile.data.source.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderApi {

    @GET("api/mobile/menu")
    Call<ResponseBody> getMenu(
            @Query("restaurantID") int restaurantId
    );

    @POST("api/mobile/orders")
    Call<ResponseBody> createOrder(
            @Header("Authorization") String token,
            @Field("restaurantId") int restaurantId,
            @Field("tableId") int tableId,
            @Body String itemsJson,
            @Field("note") String note
    );

    @GET("api/mobile/orders/my")
    Call<ResponseBody> getMyOrders(
            @Header("Authorizations") String bearerToken
    );
}
