package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.data.model.MenuItem;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.model.PaymentResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderApi {

    @GET("api/mobile/menu")
    Call<List<MenuItem>> getMenu(
            @Query("restaurantId") int restaurantId
    );

    @FormUrlEncoded
    @POST("api/mobile/orders")
    Call<Order> createOrder(
            @Field("tableId") String tableId,
            @Field("restaurantId") String restaurantId,
            @Field("note") String note
    );

    @GET("api/mobile/orders/items")
    Call<Order> getOrderItems(
            @Query("orderId") String orderId
    );

    @FormUrlEncoded
    @POST("api/mobile/orders/items/add")
    Call<Order> addItem(
            @Field("orderId") String orderId,
            @Field("foodId") String foodId,
            @Field("quantity") int quantity,
            @Field("note") String note
    );

    @FormUrlEncoded
    @POST("api/mobile/orders/items/update")
    Call<Order> updateItemQuantity(
            @Field("orderId") String orderId,
            @Field("itemId") String itemId,
            @Field("quantity") int quantity
    );

    @FormUrlEncoded
    @POST("api/mobile/orders/items/remove")
    Call<Order> removeItem(
            @Field("orderId") String orderId,
            @Field("itemId") String itemId
    );

    @FormUrlEncoded
    @POST("api/mobile/orders/status")
    Call<Order> updateOrderStatus(
            @Field("orderId") String orderId,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("api/mobile/orders/pay")
    Call<PaymentResult> payOrder(
            @Field("orderId") String orderId,
            @Field("method") String method,
            @Field("amount") Double amount
    );

    @GET("api/mobile/orders/my")
    Call<List<Order>> getMyOrders(
            @Header("Authorization") String bearerToken
    );
}
