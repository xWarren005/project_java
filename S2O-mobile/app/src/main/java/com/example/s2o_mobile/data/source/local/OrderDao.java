package com.example.s2o_mobile.data.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.s2o_mobile.data.model.Order;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(Order order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrders(List<Order> orders);

    @Query("SELECT * FROM `order` ORDER BY createdAt DESC")
    List<Order> getAllOrders();

    @Query("SELECT * FROM `order` WHERE id = :orderId LIMIT 1")
    Order getOrderById(String orderId);

    @Query("DELETE FROM `order`")
    void clearAll();
}
