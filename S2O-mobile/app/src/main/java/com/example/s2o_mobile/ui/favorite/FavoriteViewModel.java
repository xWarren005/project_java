package com.example.s2o_mobile.ui.favorite;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FavoriteViewModel extends AndroidViewModel {

    private static final String PREF_NAME = "s2o_prefs";
    private static final String KEY_FAVORITES = "favorite_restaurant_ids";

    private final SharedPreferences prefs;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(PREF_NAME, Application.MODE_PRIVATE);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public Set<String> getFavoriteIds() {
        Set<String> set = prefs.getStringSet(KEY_FAVORITES, null);
        return set == null ? Collections.emptySet() : new HashSet<>(set);
    }

    public boolean isFavorite(@NonNull String restaurantId) {
        if (restaurantId.trim().isEmpty()) return false;
        return getFavoriteIds().contains(restaurantId.trim());
    }

    public void addFavorite(@NonNull String restaurantId) {
        String id = restaurantId.trim();
        if (id.isEmpty()) return;

        Set<String> set = new HashSet<>(getFavoriteIds());
        set.add(id);
        save(set);
    }

    public void removeFavorite(@NonNull String restaurantId) {
        String id = restaurantId.trim();
        if (id.isEmpty()) return;

        Set<String> set = new HashSet<>(getFavoriteIds());
        set.remove(id);
        save(set);
    }

    public void toggleFavorite(@NonNull String restaurantId) {
        String id = restaurantId.trim();
        if (id.isEmpty()) return;

        Set<String> set = new HashSet<>(getFavoriteIds());
        if (set.contains(id)) {
            set.remove(id);
        } else {
            set.add(id);
        }
        save(set);
    }

    private void save(Set<String> ids) {
        prefs.edit()
                .putStringSet(KEY_FAVORITES, ids == null ? new HashSet<>() : ids)
                .apply();
    }
}
