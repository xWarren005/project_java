package com.example.s2o_mobile.data.repository;

import android.content.Context;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.source.local.AppDatabase;
import com.example.s2o_mobile.data.source.local.FavoriteDao;

import java.util.List;

public class FavoriteRepository {

    private final FavoriteDao favoriteDao;

    public FavoriteRepository(Context context) {
        favoriteDao = AppDatabase.getInstance(context).favoriteDao();
    }

    public void addFavorite(Restaurant restaurant) {
        if (restaurant == null) return;
        favoriteDao.insertFavorite(restaurant);
    }

    public void removeFavorite(String restaurantId) {
        if (restaurantId == null || restaurantId.trim().isEmpty()) return;
        favoriteDao.removeFavorite(restaurantId);
    }

    public List<Restaurant> getAllFavorites() {
        return favoriteDao.getAllFavorites();
    }

    public void clearAll() {
        favoriteDao.clearAll();
    }
}
