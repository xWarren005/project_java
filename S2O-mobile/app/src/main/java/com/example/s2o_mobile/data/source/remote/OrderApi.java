package com.example.s2o_mobile.data.source.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderApi {

    @GET("api/mobile/menu")
    Call<ResponseBody> getMenu(
            @Query("restaurantId") int restaurantId
    );

    @FormUrlEncoded
    @POST("api/mobile/orders")
    Call<ResponseBody> createOrder(
            @Header("Authorization") String bearerToken,
            @Field("restaurantId") int restaurantId,
            @Field("tableId") int tableId,
            @Field("itemsJson") String itemsJson,
            @Field("note") String note
    );

    @GET("api/mobile/orders/my")
    Call<ResponseBody> getMyOrders(
            @Header("Authorization") String bearerToken
    );
}
