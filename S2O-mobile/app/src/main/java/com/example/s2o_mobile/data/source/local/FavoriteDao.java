package com.example.s2o_mobile.data.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.s2o_mobile.data.model.Restaurant;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(Restaurant restaurant);

    @Query("SELECT * FROM restaurant")
    List<Restaurant> getAllFavorites();

    @Query("DELETE FROM restaurant WHERE id = :restaurantId")
    void removeFavorite(String restaurantId);

    @Query("DELETE FROM restaurant")
    void clearAll();
}
