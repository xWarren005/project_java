package com.example.s2o_mobile.data.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.s2o_mobile.data.model.Booking;

import java.util.List;

@Dao
public interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBooking(Booking booking);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookings(List<Booking> bookings);

    @Query("SELECT * FROM booking ORDER BY createdAt DESC")
    List<Booking> getAllBookings();

    @Query("SELECT * FROM booking WHERE id = :bookingId LIMIT 1")
    Booking getBookingById(String bookingId);

    @Query("DELETE FROM booking")
    void clearAll();
}
