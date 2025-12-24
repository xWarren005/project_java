package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.data.model.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface BookingApi {

    @POST("/api/reservations")
    Call<Reservation> createReservation(@Body Reservation reservation);
    
    @GET("/api/reservations")
    Call<List<Reservation>> getReservationsByUser(@Query("userId") int userId);

