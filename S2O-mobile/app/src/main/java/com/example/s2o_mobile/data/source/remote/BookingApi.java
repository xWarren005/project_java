package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.model.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface BookingApi {

    @FormUrlEncoded
    @POST("api/bookings")
    Call<Booking> createBooking(
            @Field("restaurantId") String restaurantId,
            @Field("bookingTime") String bookingTime,
            @Field("guestCount") int guestCount,
            @Field("note") String note
    );

    @GET("api/bookings/{id}")
    Call<Booking> getBookingStatus(@Path("id") String bookingId);

    @POST("/api/reservations")
    Call<Reservation> createReservation(@Body Reservation reservation);

    @GET("/api/reservations")
    Call<List<Reservation>> getReservationsByUser(@Query("userId") int userId);

    @GET("/api/reservations")
    Call<List<Reservation>> getReservationsByRestaurant(@Query("restaurantId") int restaurantId);

    @GET("/api/reservations/{id}")
    Call<Reservation> getReservationById(@Path("id") int reservationId);

    @POST("/api/reservations/{id}/cancel")
    Call<Reservation> cancelReservation(@Path("id") int reservationId);
}
