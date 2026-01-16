package com.example.s2o_mobile.data.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FavoriteRepository {

    private final Set<String> favorites = new HashSet<>();

    public void addFavorite(String restaurantId) {
        if (restaurantId == null || restaurantId.trim().isEmpty()) return;
        favorites.add(restaurantId.trim());
    }

    public void removeFavorite(String restaurantId) {
        if (restaurantId == null || restaurantId.trim().isEmpty()) return;
        favorites.remove(restaurantId.trim());
    }

    public Set<String> getFavorites() {
        return Collections.unmodifiableSet(favorites);
    }

    public boolean isFavorite(String restaurantId) {
        if (restaurantId == null || restaurantId.trim().isEmpty()) return false;
        return favorites.contains(restaurantId.trim());
    }
}